/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.apache.airavata.gfac.core.cpi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.airavata.client.api.AiravataAPI;
import org.apache.airavata.common.exception.ApplicationSettingsException;
import org.apache.airavata.common.utils.AiravataZKUtils;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.commons.gfac.type.ApplicationDescription;
import org.apache.airavata.commons.gfac.type.HostDescription;
import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.gfac.Constants;
import org.apache.airavata.gfac.GFacConfiguration;
import org.apache.airavata.gfac.GFacException;
import org.apache.airavata.gfac.Scheduler;
import org.apache.airavata.gfac.core.context.ApplicationContext;
import org.apache.airavata.gfac.core.context.JobExecutionContext;
import org.apache.airavata.gfac.core.context.MessageContext;
import org.apache.airavata.gfac.core.handler.*;
import org.apache.airavata.gfac.core.monitor.*;
import org.apache.airavata.gfac.core.monitor.state.ExperimentStatusChangeRequest;
import org.apache.airavata.gfac.core.monitor.state.GfacExperimentStateChangeRequest;
import org.apache.airavata.gfac.core.monitor.state.JobStatusChangeRequest;
import org.apache.airavata.gfac.core.monitor.state.TaskStatusChangeRequest;
import org.apache.airavata.gfac.core.notification.MonitorPublisher;
import org.apache.airavata.gfac.core.notification.events.ExecutionFailEvent;
import org.apache.airavata.gfac.core.notification.listeners.LoggingListener;
import org.apache.airavata.gfac.core.notification.listeners.WorkflowTrackingListener;
import org.apache.airavata.gfac.core.provider.GFacProvider;
import org.apache.airavata.gfac.core.provider.GFacProviderException;
import org.apache.airavata.gfac.core.provider.GFacRecoverableProvider;
import org.apache.airavata.gfac.core.scheduler.HostScheduler;
import org.apache.airavata.gfac.core.states.GfacPluginState;
import org.apache.airavata.gfac.core.utils.GFacUtils;
import org.apache.airavata.gfac.core.states.GfacExperimentState;
import org.apache.airavata.model.workspace.experiment.*;
import org.apache.airavata.registry.api.AiravataRegistry2;
import org.apache.airavata.registry.cpi.RegistryModelType;
import org.apache.airavata.registry.cpi.Registry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

/**
 * This is the GFac CPI class for external usage, this simply have a single method to submit a job to
 * the resource, required data for the job has to be stored in registry prior to invoke this object.
 */
public class BetterGfacImpl implements GFac {
    private static final Logger log = LoggerFactory.getLogger(GFacImpl.class);
    public static final String ERROR_SENT = "ErrorSent";

    private Registry registry;

    private AiravataAPI airavataAPI;

    private AiravataRegistry2 airavataRegistry2;

    private ZooKeeper zk;                       // we are not storing zk instance in to jobExecution context

    private static List<ThreadedHandler> daemonHandlers = new ArrayList<ThreadedHandler>();

    private static File gfacConfigFile;

    private static List<AbstractActivityListener> activityListeners = new ArrayList<AbstractActivityListener>();

    private static MonitorPublisher monitorPublisher;

    /**
     * Constructor for GFac
     *
     * @param registry
     * @param airavataAPI
     * @param airavataRegistry2
     * @param zooKeeper
     */
    public BetterGfacImpl(Registry registry, AiravataAPI airavataAPI, AiravataRegistry2 airavataRegistry2, ZooKeeper zooKeeper,
                          MonitorPublisher publisher) {
        this.registry = registry;
        this.airavataAPI = airavataAPI;
        this.airavataRegistry2 = airavataRegistry2;
        monitorPublisher = publisher;     // This is a EventBus common for gfac
        this.zk = zooKeeper;
    }

    public static void startStatusUpdators(Registry registry, ZooKeeper zk, MonitorPublisher publisher) {
        try {
            String[] listenerClassList = ServerSettings.getActivityListeners();
            for (String listenerClass : listenerClassList) {
                Class<? extends AbstractActivityListener> aClass = Class.forName(listenerClass).asSubclass(AbstractActivityListener.class);
                AbstractActivityListener abstractActivityListener = aClass.newInstance();
                activityListeners.add(abstractActivityListener);
                abstractActivityListener.setup(publisher, registry, zk);
                log.info("Registering listener: " + listenerClass);
                publisher.registerListener(abstractActivityListener);
            }
        } catch (ClassNotFoundException e) {
            log.error("Error loading the listener classes configured in airavata-server.properties", e);
        } catch (InstantiationException e) {
            log.error("Error loading the listener classes configured in airavata-server.properties", e);
        } catch (IllegalAccessException e) {
            log.error("Error loading the listener classes configured in airavata-server.properties", e);
        } catch (ApplicationSettingsException e) {
            log.error("Error loading the listener classes configured in airavata-server.properties", e);
        }
    }

    public static void startDaemonHandlers() {
        List<GFacHandlerConfig> daemonHandlerConfig = null;
        URL resource = GFacImpl.class.getClassLoader().getResource(org.apache.airavata.common.utils.Constants.GFAC_CONFIG_XML);
        gfacConfigFile = new File(resource.getPath());
        try {
            daemonHandlerConfig = GFacConfiguration.getDaemonHandlers(gfacConfigFile);
        } catch (ParserConfigurationException e) {
            log.error("Error parsing gfac-config.xml, double check the xml configuration", e);
        } catch (IOException e) {
            log.error("Error parsing gfac-config.xml, double check the xml configuration", e);
        } catch (SAXException e) {
            log.error("Error parsing gfac-config.xml, double check the xml configuration", e);
        } catch (XPathExpressionException e) {
            log.error("Error parsing gfac-config.xml, double check the xml configuration", e);
        }

        for (GFacHandlerConfig handlerConfig : daemonHandlerConfig) {
            String className = handlerConfig.getClassName();
            try {
                Class<?> aClass = Class.forName(className).asSubclass(ThreadedHandler.class);
                ThreadedHandler threadedHandler = (ThreadedHandler) aClass.newInstance();
                threadedHandler.initProperties(handlerConfig.getProperties());
                daemonHandlers.add(threadedHandler);
            } catch (ClassNotFoundException e) {
                log.error("Error initializing the handler: " + className);
                log.error(className + " class has to implement " + ThreadedHandler.class);
            } catch (InstantiationException e) {
                log.error("Error initializing the handler: " + className);
                log.error(className + " class has to implement " + ThreadedHandler.class);
            } catch (IllegalAccessException e) {
                log.error("Error initializing the handler: " + className);
                log.error(className + " class has to implement " + ThreadedHandler.class);
            } catch (GFacHandlerException e) {
                log.error("Error initializing the handler " + className);
            } catch (GFacException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        for (ThreadedHandler tHandler : daemonHandlers) {
            (new Thread(tHandler)).start();
        }
    }

    /**
     * This can be used to submit jobs for testing purposes just by filling parameters by hand (JobExecutionContext)
     */
    public BetterGfacImpl() {
        daemonHandlers = new ArrayList<ThreadedHandler>();
        startDaemonHandlers();
    }

    /**
     * This is the job launching method outsiders of GFac can use, this will invoke the GFac handler chain and providers
     * And update the registry occordingly, so the users can query the database to retrieve status and output from Registry
     *
     * @param experimentID
     * @return
     * @throws GFacException
     */
    public boolean submitJob(String experimentID, String taskID) throws GFacException {
        JobExecutionContext jobExecutionContext = null;
        try {
            jobExecutionContext = createJEC(experimentID, taskID);
            return submitJob(jobExecutionContext);
        } catch (Exception e) {
            log.error("Error inovoking the job with experiment ID: " + experimentID);
            throw new GFacException(e);
        }
    }

    private JobExecutionContext createJEC(String experimentID, String taskID) throws Exception {
        JobExecutionContext jobExecutionContext;
        TaskDetails taskData = (TaskDetails) registry.get(RegistryModelType.TASK_DETAIL, taskID);

        // this is wear our new model and old model is mapping (so serviceName in ExperimentData and service name in ServiceDescriptor
        // has to be same.

        // 1. Get the Task from the task ID and construct the Job object and save it in to registry
        // 2. Add another property to jobExecutionContext and read them inside the provider and use it.
        String serviceName = taskData.getApplicationId();
        if (serviceName == null) {
            throw new GFacException("Error executing the job because there is not Application Name in this Experiment:  " + serviceName);
        }

        ServiceDescription serviceDescription = airavataRegistry2.getServiceDescriptor(serviceName);
        if (serviceDescription == null) {
            throw new GFacException("Error executing the job because there is not Application Name in this Experiment:  " + serviceName);
        }
        String hostName;
        HostDescription hostDescription = null;
        if (taskData.getTaskScheduling().getResourceHostId() != null) {
            hostName = taskData.getTaskScheduling().getResourceHostId();
            hostDescription = airavataRegistry2.getHostDescriptor(hostName);
        } else {
            List<HostDescription> registeredHosts = new ArrayList<HostDescription>();
            Map<String, ApplicationDescription> applicationDescriptors = airavataRegistry2.getApplicationDescriptors(serviceName);
            for (String hostDescName : applicationDescriptors.keySet()) {
                registeredHosts.add(airavataRegistry2.getHostDescriptor(hostDescName));
            }
            Class<? extends HostScheduler> aClass = Class.forName(ServerSettings.getHostScheduler()).asSubclass(HostScheduler.class);
            HostScheduler hostScheduler = aClass.newInstance();
            hostDescription = hostScheduler.schedule(registeredHosts);
            hostName = hostDescription.getType().getHostName();
        }
        if (hostDescription == null) {
            throw new GFacException("Error executing the job as the host is not registered " + hostName);
        }
        ApplicationDescription applicationDescription = airavataRegistry2.getApplicationDescriptors(serviceName, hostName);
        URL resource = GFacImpl.class.getClassLoader().getResource(org.apache.airavata.common.utils.Constants.GFAC_CONFIG_XML);
        Properties configurationProperties = ServerSettings.getProperties();
        GFacConfiguration gFacConfiguration = GFacConfiguration.create(new File(resource.getPath()), airavataAPI, configurationProperties);


        // start constructing jobexecutioncontext
        jobExecutionContext = new JobExecutionContext(gFacConfiguration, serviceName);

        // setting experiment/task/workflownode related information
        Experiment experiment = (Experiment) registry.get(RegistryModelType.EXPERIMENT, experimentID);
        jobExecutionContext.setExperiment(experiment);
        jobExecutionContext.setExperimentID(experimentID);
        jobExecutionContext.setWorkflowNodeDetails(experiment.getWorkflowNodeDetailsList().get(0));
        jobExecutionContext.setTaskData(taskData);

        // setting the registry
        jobExecutionContext.setRegistry(registry);

        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setApplicationDeploymentDescription(applicationDescription);
        applicationContext.setHostDescription(hostDescription);
        applicationContext.setServiceDescription(serviceDescription);
        jobExecutionContext.setApplicationContext(applicationContext);

        List<DataObjectType> experimentInputs = taskData.getApplicationInputs();
        jobExecutionContext.setInMessageContext(new MessageContext(GFacUtils.getMessageContext(experimentInputs,
                serviceDescription.getType().getInputParametersArray())));

        List<DataObjectType> outputData = taskData.getApplicationOutputs();
        jobExecutionContext.setOutMessageContext(new MessageContext(GFacUtils.getMessageContext(outputData,
                serviceDescription.getType().getOutputParametersArray())));

        jobExecutionContext.setProperty(Constants.PROP_TOPIC, experimentID);
        jobExecutionContext.setGfac(this);
        jobExecutionContext.setZk(zk);
        return jobExecutionContext;
    }

    public boolean submitJob(JobExecutionContext jobExecutionContext) throws GFacException {
        // We need to check whether this job is submitted as a part of a large workflow. If yes,
        // we need to setup workflow tracking listerner.
        try {
            int stateVal = GFacUtils.getZKExperimentStateValue(zk, jobExecutionContext);   // this is the original state came, if we query again it might be different,so we preserve this state in the environment
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext)
                    , GfacExperimentState.ACCEPTED));                  // immediately we get the request we update the status
            String workflowInstanceID = null;
            if ((workflowInstanceID = (String) jobExecutionContext.getProperty(Constants.PROP_WORKFLOW_INSTANCE_ID)) != null) {
                // This mean we need to register workflow tracking listener.
                //todo implement WorkflowTrackingListener properly
                registerWorkflowTrackingListener(workflowInstanceID, jobExecutionContext);
            }
            // Register log event listener. This is required in all scenarios.
            jobExecutionContext.getNotificationService().registerListener(new LoggingListener());
            if (stateVal < 2) {
                // In this scenario We do everything from the beginning
                launch(jobExecutionContext);
            } else if (stateVal >= 8) {
                log.info("There is nothing to recover in this job so we do not re-submit");
                ZKUtil.deleteRecursive(zk,
                        AiravataZKUtils.getExpZnodePath(jobExecutionContext.getExperimentID(), jobExecutionContext.getTaskData().getTaskID()));
            } else {
                // Now we know this is an old Job, so we have to handle things gracefully
                log.info("Re-launching the job in GFac because this is re-submitted to GFac");
                reLaunch(jobExecutionContext, stateVal);
            }
            return true;
        } catch (ApplicationSettingsException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void reLaunch(JobExecutionContext jobExecutionContext, int stateVal) throws GFacException {
        // Scheduler will decide the execution flow of handlers and provider which handles
        // the job.
        String experimentID = jobExecutionContext.getExperimentID();
        try {
            Scheduler.schedule(jobExecutionContext);

            // Executing in handlers in the order as they have configured in GFac configuration
            // here we do not skip handler if some handler does not have to be run again during re-run it can implement
            // that logic in to the handler
            reInvokeInFlowHandlers(jobExecutionContext);

            // After executing the in handlers provider instance should be set to job execution context.
            // We get the provider instance and execute it.
            if (stateVal == 2 || stateVal == 3) {
                invokeProvider(jobExecutionContext);     // provider never ran in previous invocation
            } else if (stateVal == 4) {   // whether sync or async job have to invoke the recovering because it crashed in the Handler
                reInvokeProvider(jobExecutionContext);
            } else if (stateVal >= 5 && GFacUtils.isSynchronousMode(jobExecutionContext)) {
                // In this case we do nothing because provider ran successfully, no need to re-run the job
                log.info("Provider does not have to be recovered because it ran successfully for experiment: " + experimentID);
            } else if (stateVal == 5 && !GFacUtils.isSynchronousMode(jobExecutionContext)) {
                // this is async mode where monitoring of jobs is hapenning, we have to recover
                reInvokeProvider(jobExecutionContext);
            } else {
                log.info("We skip invoking Handler, because the experiment state is beyond the Provider Invocation !!!");
                log.info("ExperimentId: " + experimentID + " taskId: " + jobExecutionContext.getTaskData().getTaskID());
            }
        } catch (Exception e) {
            try {
                // we make the experiment as failed due to exception scenario
                monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.FAILED));
                monitorPublisher.publish(new
                        ExperimentStatusChangeRequest(new ExperimentIdentity(jobExecutionContext.getExperimentID()),
                        ExperimentState.FAILED));
                // Updating the task status if there's any task associated
                monitorPublisher.publish(new TaskStatusChangeRequest(
                        new TaskIdentity(jobExecutionContext.getExperimentID(),
                                jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                                jobExecutionContext.getTaskData().getTaskID()), TaskState.FAILED
                ));
                monitorPublisher.publish(new JobStatusChangeRequest(new MonitorID(jobExecutionContext),
                        new JobIdentity(jobExecutionContext.getExperimentID(),
                                jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                                jobExecutionContext.getTaskData().getTaskID(), jobExecutionContext.getJobDetails().getJobID()), JobState.FAILED
                ));
            } catch (NullPointerException e1) {
                log.error("Error occured during updating the statuses of Experiments,tasks or Job statuses to failed, " +
                        "NullPointerException occurred because at this point there might not have Job Created", e1, e);
            }
            jobExecutionContext.setProperty(ERROR_SENT, "true");
            jobExecutionContext.getNotifier().publish(new ExecutionFailEvent(e.getCause()));
            throw new GFacException(e.getMessage(), e);
        }
    }

    private void launch(JobExecutionContext jobExecutionContext) throws GFacException {
        // Scheduler will decide the execution flow of handlers and provider which handles
        // the job.
        try {
            Scheduler.schedule(jobExecutionContext);

            // Executing in handlers in the order as they have configured in GFac configuration
            // here we do not skip handler if some handler does not have to be run again during re-run it can implement
            // that logic in to the handler
            invokeInFlowHandlers(jobExecutionContext);               // to keep the consistency we always try to re-run to avoid complexity
            //            if (experimentID != null){
            //                registry2.changeStatus(jobExecutionContext.getExperimentID(),AiravataJobState.State.INHANDLERSDONE);
            //            }

            // After executing the in handlers provider instance should be set to job execution context.
            // We get the provider instance and execute it.
            invokeProvider(jobExecutionContext);
        } catch (Exception e) {
            try {
                // we make the experiment as failed due to exception scenario
                monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.FAILED));
                monitorPublisher.publish(new
                        ExperimentStatusChangeRequest(new ExperimentIdentity(jobExecutionContext.getExperimentID()),
                        ExperimentState.FAILED));
                // Updating the task status if there's any task associated
                monitorPublisher.publish(new TaskStatusChangeRequest(
                        new TaskIdentity(jobExecutionContext.getExperimentID(),
                                jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                                jobExecutionContext.getTaskData().getTaskID()), TaskState.FAILED
                ));
                monitorPublisher.publish(new JobStatusChangeRequest(new MonitorID(jobExecutionContext),
                        new JobIdentity(jobExecutionContext.getExperimentID(),
                                jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                                jobExecutionContext.getTaskData().getTaskID(), jobExecutionContext.getJobDetails().getJobID()), JobState.FAILED
                ));
            } catch (NullPointerException e1) {
                log.error("Error occured during updating the statuses of Experiments,tasks or Job statuses to failed, " +
                        "NullPointerException occurred because at this point there might not have Job Created", e1, e);
            }
            jobExecutionContext.setProperty(ERROR_SENT, "true");
            jobExecutionContext.getNotifier().publish(new ExecutionFailEvent(e.getCause()));
            throw new GFacException(e.getMessage(), e);
        }
    }

    private void invokeProvider(JobExecutionContext jobExecutionContext) throws GFacException, ApplicationSettingsException, InterruptedException, KeeperException {
        GFacProvider provider = jobExecutionContext.getProvider();
        if (provider != null) {
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.PROVIDERINVOKING));
            GFacUtils.createPluginZnode(zk, jobExecutionContext, provider.getClass().getName());
            initProvider(provider, jobExecutionContext);
            executeProvider(provider, jobExecutionContext);
            disposeProvider(provider, jobExecutionContext);
            GFacUtils.updatePluginState(zk, jobExecutionContext, provider.getClass().getName(), GfacPluginState.COMPLETED);
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.PROVIDERINVOKED));
        }
        if (GFacUtils.isSynchronousMode(jobExecutionContext)) {
            invokeOutFlowHandlers(jobExecutionContext);
        }
    }

    private void reInvokeProvider(JobExecutionContext jobExecutionContext) throws GFacException, GFacProviderException, ApplicationSettingsException, InterruptedException, KeeperException {
        GFacProvider provider = jobExecutionContext.getProvider();
        if (provider != null) {
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.PROVIDERINVOKING));
            String plState = GFacUtils.getPluginState(zk, jobExecutionContext, provider.getClass().getName());
            if (Integer.valueOf(plState) >= GfacPluginState.INVOKED.getValue()) {    // this will make sure if a plugin crashes it will not launch from the scratch, but plugins have to save their invoked state
                if (provider instanceof GFacRecoverableProvider) {
                    GFacUtils.createPluginZnode(zk, jobExecutionContext, provider.getClass().getName());
                    ((GFacRecoverableProvider) provider).recover(jobExecutionContext);
                    GFacUtils.updatePluginState(zk, jobExecutionContext, provider.getClass().getName(), GfacPluginState.COMPLETED);
                }
            } else {
                GFacUtils.createPluginZnode(zk, jobExecutionContext, provider.getClass().getName());
                initProvider(provider, jobExecutionContext);
                executeProvider(provider, jobExecutionContext);
                disposeProvider(provider, jobExecutionContext);
                GFacUtils.updatePluginState(zk, jobExecutionContext, provider.getClass().getName(), GfacPluginState.COMPLETED);
            }
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.PROVIDERINVOKED));
        }

        if (GFacUtils.isSynchronousMode(jobExecutionContext))

        {
            invokeOutFlowHandlers(jobExecutionContext);
        }

    }


    private void initProvider(GFacProvider provider, JobExecutionContext jobExecutionContext) throws GFacException {
        try {
            provider.initialize(jobExecutionContext);
        } catch (Exception e) {
            throw new GFacException("Error while initializing provider " + provider.getClass().getName() + ".", e);
        }
    }

    private void executeProvider(GFacProvider provider, JobExecutionContext jobExecutionContext) throws GFacException {
        try {
            provider.execute(jobExecutionContext);
        } catch (Exception e) {
            throw new GFacException("Error while executing provider " + provider.getClass().getName() + " functionality.", e);
        }
    }

    private void disposeProvider(GFacProvider provider, JobExecutionContext jobExecutionContext) throws GFacException {
        try {
            provider.dispose(jobExecutionContext);
        } catch (Exception e) {
            throw new GFacException("Error while invoking provider " + provider.getClass().getName() + " dispose method.", e);
        }
    }

    private void registerWorkflowTrackingListener(String workflowInstanceID, JobExecutionContext jobExecutionContext) {
        String workflowNodeID = (String) jobExecutionContext.getProperty(Constants.PROP_WORKFLOW_NODE_ID);
        String topic = (String) jobExecutionContext.getProperty(Constants.PROP_TOPIC);
        String brokerUrl = (String) jobExecutionContext.getProperty(Constants.PROP_BROKER_URL);
        jobExecutionContext.getNotificationService().registerListener(
                new WorkflowTrackingListener(workflowInstanceID, workflowNodeID, brokerUrl, topic));

    }

    private void invokeInFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException {
        List<GFacHandlerConfig> handlers = jobExecutionContext.getGFacConfiguration().getInHandlers();
        try {
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext)
                    , GfacExperimentState.INHANDLERSINVOKING));
            for (GFacHandlerConfig handlerClassName : handlers) {
                Class<? extends GFacHandler> handlerClass;
                GFacHandler handler;
                try {
                    GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName());
                    handlerClass = Class.forName(handlerClassName.getClassName().trim()).asSubclass(GFacHandler.class);
                    handler = handlerClass.newInstance();
                    handler.initProperties(handlerClassName.getProperties());
                } catch (ClassNotFoundException e) {
                    throw new GFacException("Cannot load handler class " + handlerClassName, e);
                } catch (InstantiationException e) {
                    throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
                } catch (IllegalAccessException e) {
                    throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
                }
                try {
                    handler.invoke(jobExecutionContext);
                    GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
                    // if exception thrown before that we do not make it finished
                } catch (GFacHandlerException e) {
                    throw new GFacException("Error Executing a InFlow Handler", e.getCause());
                }
            }
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext)
                    , GfacExperimentState.INHANDLERSINVOKED));
        } catch (Exception e) {
            throw new GFacException("Error invoking ZK", e);
        }
    }

    public void invokeOutFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException {
        GFacConfiguration gFacConfiguration = jobExecutionContext.getGFacConfiguration();
        List<GFacHandlerConfig> handlers = null;
        if (gFacConfiguration != null) {
            handlers = jobExecutionContext.getGFacConfiguration().getOutHandlers();
        } else {
            try {
                jobExecutionContext = createJEC(jobExecutionContext.getExperimentID(), jobExecutionContext.getTaskData().getTaskID());
            } catch (Exception e) {
                log.error("Error constructing job execution context during outhandler invocation");
                throw new GFacException(e);
            }
            launch(jobExecutionContext);
        }
        monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.OUTHANDLERSINVOKING));
        for (GFacHandlerConfig handlerClassName : handlers) {
            Class<? extends GFacHandler> handlerClass;
            GFacHandler handler;
            try {
                GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName());
                handlerClass = Class.forName(handlerClassName.getClassName().trim()).asSubclass(GFacHandler.class);
                handler = handlerClass.newInstance();
                handler.initProperties(handlerClassName.getProperties());
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot load handler class " + handlerClassName, e);
            } catch (InstantiationException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
            } catch (Exception e) {
                throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
            }
            try {
                handler.invoke(jobExecutionContext);
                GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
            } catch (Exception e) {
                // TODO: Better error reporting.
                throw new GFacException("Error Executing a OutFlow Handler", e);
            }
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.OUTHANDLERSINVOKED));
        }

        // At this point all the execution is finished so we update the task and experiment statuses.
        // Handler authors does not have to worry about updating experiment or task statuses.
        monitorPublisher.publish(new
                ExperimentStatusChangeRequest(new ExperimentIdentity(jobExecutionContext.getExperimentID()),
                ExperimentState.COMPLETED));
        // Updating the task status if there's any task associated
        monitorPublisher.publish(new TaskStatusChangeRequest(
                new TaskIdentity(jobExecutionContext.getExperimentID(),
                        jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                        jobExecutionContext.getTaskData().getTaskID()), TaskState.COMPLETED
        ));
        monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.COMPLETED));
    }

    /**
     * If handlers ran successfully we re-run only recoverable handlers
     * If handler never ran we run the normal invoke method
     *
     * @param jobExecutionContext
     * @throws GFacException
     */
    private void reInvokeInFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException {
        List<GFacHandlerConfig> handlers = jobExecutionContext.getGFacConfiguration().getInHandlers();
        try {
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext)
                    , GfacExperimentState.INHANDLERSINVOKING));
            for (GFacHandlerConfig handlerClassName : handlers) {
                Class<? extends GFacHandler> handlerClass;
                GFacHandler handler;
                try {
                    handlerClass = Class.forName(handlerClassName.getClassName().trim()).asSubclass(GFacHandler.class);
                    handler = handlerClass.newInstance();
                    String plState = GFacUtils.getPluginState(zk, jobExecutionContext, handlerClassName.getClassName());
                    int state = 0;
                    try {
                        state = Integer.valueOf(plState);
                    } catch (NumberFormatException e) {

                    }
                    if (state >= GfacPluginState.INVOKED.getValue()) {
                        if (handler instanceof GFacRecoverableHandler) {
                            // if these already ran we re-run only recoverable handlers
                            log.info(handlerClassName.getClassName() + " is a recoverable handler so we recover the handler");
                            GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.INVOKING);
                            ((GFacRecoverableHandler) handler).recover(jobExecutionContext);
                            GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
                        } else {
                            log.info(handlerClassName.getClassName() + " is not a recoverable handler so we do not run because it already ran in last-run");
                        }
                    } else {
                        log.info(handlerClassName.getClassName() + " never ran so we run this is normal mode");
                        GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.INVOKING);
                        handler.initProperties(handlerClassName.getProperties());
                        handler.invoke(jobExecutionContext);
                        GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
                    }
                } catch (GFacHandlerException e) {
                    throw new GFacException("Error Executing a InFlow Handler", e.getCause());
                } catch (ClassNotFoundException e) {
                    throw new GFacException("Cannot load handler class " + handlerClassName, e);
                } catch (InstantiationException e) {
                    throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
                } catch (IllegalAccessException e) {
                    throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
                }
            }
            monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext)
                    , GfacExperimentState.INHANDLERSINVOKED));
        } catch (Exception e) {
            throw new GFacException("Error invoking ZK", e);
        }
    }

    public void reInvokeOutFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException {
        GFacConfiguration gFacConfiguration = jobExecutionContext.getGFacConfiguration();
        List<GFacHandlerConfig> handlers = null;
        if (gFacConfiguration != null) {
            handlers = jobExecutionContext.getGFacConfiguration().getOutHandlers();
        } else {
            try {
                jobExecutionContext = createJEC(jobExecutionContext.getExperimentID(), jobExecutionContext.getTaskData().getTaskID());
            } catch (Exception e) {
                log.error("Error constructing job execution context during outhandler invocation");
                throw new GFacException(e);
            }
            launch(jobExecutionContext);
        }
        monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.OUTHANDLERSINVOKING));
        for (GFacHandlerConfig handlerClassName : handlers) {
            Class<? extends GFacHandler> handlerClass;
            GFacHandler handler;
            try {
                handlerClass = Class.forName(handlerClassName.getClassName().trim()).asSubclass(GFacHandler.class);
                handler = handlerClass.newInstance();
                String plState = GFacUtils.getPluginState(zk, jobExecutionContext, handlerClassName.getClassName());
                if (Integer.valueOf(plState) >= GfacPluginState.INVOKED.getValue()) {
                    if (handler instanceof GFacRecoverableHandler) {
                        // if these already ran we re-run only recoverable handlers
                        log.info(handlerClassName.getClassName() + " is a recoverable handler so we recover the handler");
                        GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.INVOKING);
                        ((GFacRecoverableHandler) handler).recover(jobExecutionContext);
                        GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
                    } else {
                        log.info(handlerClassName.getClassName() + " is not a recoverable handler so we do not run because it already ran in last-run");
                    }
                } else {
                    log.info(handlerClassName.getClassName() + " never ran so we run this is normal mode");
                    GFacUtils.createPluginZnode(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.INVOKING);
                    handler.initProperties(handlerClassName.getProperties());
                    handler.invoke(jobExecutionContext);
                    GFacUtils.updatePluginState(zk, jobExecutionContext, handlerClassName.getClassName(), GfacPluginState.COMPLETED);
                }
                handler.initProperties(handlerClassName.getProperties());
                handler.invoke(jobExecutionContext);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot load handler class " + handlerClassName, e);
            } catch (InstantiationException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new GFacException("Cannot instantiate handler class " + handlerClassName, e);
            } catch (Exception e) {
                // TODO: Better error reporting.
                throw new GFacException("Error Executing a OutFlow Handler", e);
            }
        }
        monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.OUTHANDLERSINVOKED));

        // At this point all the execution is finished so we update the task and experiment statuses.
        // Handler authors does not have to worry about updating experiment or task statuses.
        monitorPublisher.publish(new
                ExperimentStatusChangeRequest(new ExperimentIdentity(jobExecutionContext.getExperimentID()),
                ExperimentState.COMPLETED));
        // Updating the task status if there's any task associated
        monitorPublisher.publish(new TaskStatusChangeRequest(
                new TaskIdentity(jobExecutionContext.getExperimentID(),
                        jobExecutionContext.getWorkflowNodeDetails().getNodeInstanceId(),
                        jobExecutionContext.getTaskData().getTaskID()), TaskState.COMPLETED
        ));
        monitorPublisher.publish(new GfacExperimentStateChangeRequest(new MonitorID(jobExecutionContext), GfacExperimentState.COMPLETED));
    }


    public static void setMonitorPublisher(MonitorPublisher monitorPublisher) {
        BetterGfacImpl.monitorPublisher = monitorPublisher;
    }

    public AiravataAPI getAiravataAPI() {
        return airavataAPI;
    }

    public AiravataRegistry2 getAiravataRegistry2() {
        return airavataRegistry2;
    }

    public static List<ThreadedHandler> getDaemonHandlers() {
        return daemonHandlers;
    }

    public static String getErrorSent() {
        return ERROR_SENT;
    }

    public File getGfacConfigFile() {
        return gfacConfigFile;
    }

    public static MonitorPublisher getMonitorPublisher() {
        return monitorPublisher;
    }

    public Registry getRegistry() {
        return registry;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }
}