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

package org.apache.airavata.persistence.appcatalog.jpa.model;

import javax.persistence.*;

@Entity
@Table(name = "GSISSH_EXPORT")
@IdClass(GSISSHExportPK.class)
public class GSISSHExport {
    @Id
    @Column(name = "SUBMISSION_ID")
    private String submissionID;
    @Id
    @Column(name = "EXPORT")
    private String export;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "SUBMISSION_ID")
    private GSISSHSubmission gsisshJobSubmission;

    public String getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(String submissionID) {
        this.submissionID = submissionID;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public GSISSHSubmission getGsisshJobSubmission() {
        return gsisshJobSubmission;
    }

    public void setGsisshJobSubmission(GSISSHSubmission gsisshJobSubmission) {
        this.gsisshJobSubmission = gsisshJobSubmission;
    }
}