package com.haohai.platform.fireforestplatform.ui.bean;

import java.util.List;

/**
 * Created by qc
 * on 2024/2/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class EmergencyDetail {

    /**
     * remark :
     * gridNo : 370203029
     * groupNo : 66787066-1_5712a721-8668
     * deptNo :
     * delFlag : 0
     * createBy : lnlnl
     * createTime : 2024-01-12 10:44:07
     * updateBy : lnlnl
     * updateTime : 2024-02-02 09:57:48
     * params :
     * id : 1745637754265534464
     * groupName : 兴隆路街道
     * lastVersionId : 1749718668431982592
     * fileList : []
     * delIdList :
     * incidentLevel : 3
     * reportingFlag : 1
     * incidentTitle : lnlnl 上报事件
     * incidentType : 01/0101/010102
     * incidentTypeName : 自然灾害/气象灾害/龙卷风
     * incidentArea : 370203014
     * incidentTime : 2024-01-12 10:43:58
     * topFlag : 0
     * receivingGroupNo :
     * receivingUser :
     * receivingType :
     * reportingTime :
     * emergenciesType :
     * readingStatus : 1
     * emergIncidentVersion : {"remark":"","gridNo":"","groupNo":"7a338510-9968_a011a3b1-70f2","deptNo":"","delFlag":"0","createBy":"haohai","createTime":"2024-01-23 17:00:13","updateBy":"haohai","updateTime":"2024-01-23 17:00:13","params":"","id":"1749718668431982592","incidentId":"1745637754265534464","reportingStatus":"续报2","fallBackReason":"","incidentVersion":2,"incidentAddr":"山东省青岛市市南区","reportingTime":"2024-01-23 17:00:13","propertyLoss":"0","sensitive":"","death":"","injured":"","seriousInjured":"","missing":"","reporter":"","reporterPhone":"","reporterUnit":"","reporterDept":"","charger":"","chargerPhone":"","chargerUnit":"","chargerDept":"","incidentReasons":"","incidentMeasures":"","incidentDetails":"","groupName":"市北区","sensitiveFlag":"0","incidentProblems":"","incidentProgress":"","burnedArea":"","personId":"","longtitude":"120.35237668742555","latitude":"36.06609315421645","putoutTime":"","fireReason":"","serialNumber":"","topFlag":"0","incidentLevel":"3"}
     * handleStatus : 1
     * source : 兴隆路街道
     * reportSource :
     * sourceNo : 66787066-1_5712a721-8668
     * mergeId :
     * planId :
     * planName :
     * levelRemark :
     * levelSpecial :
     * fakerFlag : 1
     * reportingType :
     * personId :
     * terminalType :
     * serialNumber :
     * personLiable :
     * distributionDepartment :
     * deadline :
     * taskDescription :
     * sourceTime :
     * distance :
     * longtitude :
     * latitude :
     * geoPosition :
     * eventLabels :
     * shortMessageIds :
     * gridName :
     * incidentRealStatus : 1
     * remIncidentType : []
     * death :
     * seriousInjured :
     * injured :
     * missing :
     * incidentHourStart :
     * incidentHourEnd :
     * incidentReceive :
     * relationUserId : 1745255749581406208,
     * incidentCode :
     * incidentLink :
     * transferMessageContent :
     * auditStatus : 3
     * judgeCode :
     * typeName :
     * incidentAddr :
     * emergIncidentTypeDTO :
     * auditStatusList :
     * querySource :
     */

    private String remark;
    private String gridNo;
    private String groupNo;
    private String deptNo;
    private String delFlag;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String params;
    private String id;
    private String groupName;
    private String lastVersionId;
    private String delIdList;
    private String incidentLevel;
    private String reportingFlag;
    private String incidentTitle;
    private String incidentType;
    private String incidentTypeName;
    private String incidentArea;
    private String incidentTime;
    private String topFlag;
    private String receivingGroupNo;
    private String receivingUser;
    private String receivingType;
    private String reportingTime;
    private String emergenciesType;
    private String readingStatus;
    private EmergIncidentVersionBean emergIncidentVersion;
    private String handleStatus;
    private String source;
    private String reportSource;
    private String sourceNo;
    private String mergeId;
    private String planId;
    private String planName;
    private String levelRemark;
    private String levelSpecial;
    private String fakerFlag;
    private String reportingType;
    private String personId;
    private String terminalType;
    private String serialNumber;
    private String personLiable;
    private String distributionDepartment;
    private String deadline;
    private String taskDescription;
    private String sourceTime;
    private String distance;
    private String longtitude;
    private String latitude;
    private String geoPosition;
    private String eventLabels;
    private String shortMessageIds;
    private String gridName;
    private String incidentRealStatus;
    private String death;
    private String seriousInjured;
    private String injured;
    private String missing;
    private String incidentHourStart;
    private String incidentHourEnd;
    private String incidentReceive;
    private String relationUserId;
    private String incidentCode;
    private String incidentLink;
    private String transferMessageContent;
    private String auditStatus;
    private String judgeCode;
    private String typeName;
    private String incidentAddr;
    private String emergIncidentTypeDTO;
    private String auditStatusList;
    private String querySource;
    private List<EmergencyFile> fileList;
    private List<?> remIncidentType;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastVersionId() {
        return lastVersionId;
    }

    public void setLastVersionId(String lastVersionId) {
        this.lastVersionId = lastVersionId;
    }

    public String getDelIdList() {
        return delIdList;
    }

    public void setDelIdList(String delIdList) {
        this.delIdList = delIdList;
    }

    public String getIncidentLevel() {
        return incidentLevel;
    }

    public void setIncidentLevel(String incidentLevel) {
        this.incidentLevel = incidentLevel;
    }

    public String getReportingFlag() {
        return reportingFlag;
    }

    public void setReportingFlag(String reportingFlag) {
        this.reportingFlag = reportingFlag;
    }

    public String getIncidentTitle() {
        return incidentTitle;
    }

    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentTypeName() {
        return incidentTypeName;
    }

    public void setIncidentTypeName(String incidentTypeName) {
        this.incidentTypeName = incidentTypeName;
    }

    public String getIncidentArea() {
        return incidentArea;
    }

    public void setIncidentArea(String incidentArea) {
        this.incidentArea = incidentArea;
    }

    public String getIncidentTime() {
        return incidentTime;
    }

    public void setIncidentTime(String incidentTime) {
        this.incidentTime = incidentTime;
    }

    public String getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(String topFlag) {
        this.topFlag = topFlag;
    }

    public String getReceivingGroupNo() {
        return receivingGroupNo;
    }

    public void setReceivingGroupNo(String receivingGroupNo) {
        this.receivingGroupNo = receivingGroupNo;
    }

    public String getReceivingUser() {
        return receivingUser;
    }

    public void setReceivingUser(String receivingUser) {
        this.receivingUser = receivingUser;
    }

    public String getReceivingType() {
        return receivingType;
    }

    public void setReceivingType(String receivingType) {
        this.receivingType = receivingType;
    }

    public String getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(String reportingTime) {
        this.reportingTime = reportingTime;
    }

    public String getEmergenciesType() {
        return emergenciesType;
    }

    public void setEmergenciesType(String emergenciesType) {
        this.emergenciesType = emergenciesType;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public EmergIncidentVersionBean getEmergIncidentVersion() {
        return emergIncidentVersion;
    }

    public void setEmergIncidentVersion(EmergIncidentVersionBean emergIncidentVersion) {
        this.emergIncidentVersion = emergIncidentVersion;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReportSource() {
        return reportSource;
    }

    public void setReportSource(String reportSource) {
        this.reportSource = reportSource;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getMergeId() {
        return mergeId;
    }

    public void setMergeId(String mergeId) {
        this.mergeId = mergeId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getLevelRemark() {
        return levelRemark;
    }

    public void setLevelRemark(String levelRemark) {
        this.levelRemark = levelRemark;
    }

    public String getLevelSpecial() {
        return levelSpecial;
    }

    public void setLevelSpecial(String levelSpecial) {
        this.levelSpecial = levelSpecial;
    }

    public String getFakerFlag() {
        return fakerFlag;
    }

    public void setFakerFlag(String fakerFlag) {
        this.fakerFlag = fakerFlag;
    }

    public String getReportingType() {
        return reportingType;
    }

    public void setReportingType(String reportingType) {
        this.reportingType = reportingType;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPersonLiable() {
        return personLiable;
    }

    public void setPersonLiable(String personLiable) {
        this.personLiable = personLiable;
    }

    public String getDistributionDepartment() {
        return distributionDepartment;
    }

    public void setDistributionDepartment(String distributionDepartment) {
        this.distributionDepartment = distributionDepartment;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(String sourceTime) {
        this.sourceTime = sourceTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(String geoPosition) {
        this.geoPosition = geoPosition;
    }

    public String getEventLabels() {
        return eventLabels;
    }

    public void setEventLabels(String eventLabels) {
        this.eventLabels = eventLabels;
    }

    public String getShortMessageIds() {
        return shortMessageIds;
    }

    public void setShortMessageIds(String shortMessageIds) {
        this.shortMessageIds = shortMessageIds;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getIncidentRealStatus() {
        return incidentRealStatus;
    }

    public void setIncidentRealStatus(String incidentRealStatus) {
        this.incidentRealStatus = incidentRealStatus;
    }

    public String getDeath() {
        return death;
    }

    public void setDeath(String death) {
        this.death = death;
    }

    public String getSeriousInjured() {
        return seriousInjured;
    }

    public void setSeriousInjured(String seriousInjured) {
        this.seriousInjured = seriousInjured;
    }

    public String getInjured() {
        return injured;
    }

    public void setInjured(String injured) {
        this.injured = injured;
    }

    public String getMissing() {
        return missing;
    }

    public void setMissing(String missing) {
        this.missing = missing;
    }

    public String getIncidentHourStart() {
        return incidentHourStart;
    }

    public void setIncidentHourStart(String incidentHourStart) {
        this.incidentHourStart = incidentHourStart;
    }

    public String getIncidentHourEnd() {
        return incidentHourEnd;
    }

    public void setIncidentHourEnd(String incidentHourEnd) {
        this.incidentHourEnd = incidentHourEnd;
    }

    public String getIncidentReceive() {
        return incidentReceive;
    }

    public void setIncidentReceive(String incidentReceive) {
        this.incidentReceive = incidentReceive;
    }

    public String getRelationUserId() {
        return relationUserId;
    }

    public void setRelationUserId(String relationUserId) {
        this.relationUserId = relationUserId;
    }

    public String getIncidentCode() {
        return incidentCode;
    }

    public void setIncidentCode(String incidentCode) {
        this.incidentCode = incidentCode;
    }

    public String getIncidentLink() {
        return incidentLink;
    }

    public void setIncidentLink(String incidentLink) {
        this.incidentLink = incidentLink;
    }

    public String getTransferMessageContent() {
        return transferMessageContent;
    }

    public void setTransferMessageContent(String transferMessageContent) {
        this.transferMessageContent = transferMessageContent;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getJudgeCode() {
        return judgeCode;
    }

    public void setJudgeCode(String judgeCode) {
        this.judgeCode = judgeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIncidentAddr() {
        return incidentAddr;
    }

    public void setIncidentAddr(String incidentAddr) {
        this.incidentAddr = incidentAddr;
    }

    public String getEmergIncidentTypeDTO() {
        return emergIncidentTypeDTO;
    }

    public void setEmergIncidentTypeDTO(String emergIncidentTypeDTO) {
        this.emergIncidentTypeDTO = emergIncidentTypeDTO;
    }

    public String getAuditStatusList() {
        return auditStatusList;
    }

    public void setAuditStatusList(String auditStatusList) {
        this.auditStatusList = auditStatusList;
    }

    public String getQuerySource() {
        return querySource;
    }

    public void setQuerySource(String querySource) {
        this.querySource = querySource;
    }

    public List<EmergencyFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<EmergencyFile> fileList) {
        this.fileList = fileList;
    }

    public List<?> getRemIncidentType() {
        return remIncidentType;
    }

    public void setRemIncidentType(List<?> remIncidentType) {
        this.remIncidentType = remIncidentType;
    }

    public static class EmergIncidentVersionBean {
        /**
         * remark :
         * gridNo :
         * groupNo : 7a338510-9968_a011a3b1-70f2
         * deptNo :
         * delFlag : 0
         * createBy : haohai
         * createTime : 2024-01-23 17:00:13
         * updateBy : haohai
         * updateTime : 2024-01-23 17:00:13
         * params :
         * id : 1749718668431982592
         * incidentId : 1745637754265534464
         * reportingStatus : 续报2
         * fallBackReason :
         * incidentVersion : 2
         * incidentAddr : 山东省青岛市市南区
         * reportingTime : 2024-01-23 17:00:13
         * propertyLoss : 0
         * sensitive :
         * death :
         * injured :
         * seriousInjured :
         * missing :
         * reporter :
         * reporterPhone :
         * reporterUnit :
         * reporterDept :
         * charger :
         * chargerPhone :
         * chargerUnit :
         * chargerDept :
         * incidentReasons :
         * incidentMeasures :
         * incidentDetails :
         * groupName : 市北区
         * sensitiveFlag : 0
         * incidentProblems :
         * incidentProgress :
         * burnedArea :
         * personId :
         * longtitude : 120.35237668742555
         * latitude : 36.06609315421645
         * putoutTime :
         * fireReason :
         * serialNumber :
         * topFlag : 0
         * incidentLevel : 3
         */

        private String remark;
        private String gridNo;
        private String groupNo;
        private String deptNo;
        private String delFlag;
        private String createBy;
        private String createTime;
        private String updateBy;
        private String updateTime;
        private String params;
        private String id;
        private String incidentId;
        private String reportingStatus;
        private String fallBackReason;
        private String incidentVersion;
        private String incidentAddr;
        private String reportingTime;
        private String propertyLoss;
        private String sensitive;
        private String death;
        private String injured;
        private String seriousInjured;
        private String missing;
        private String reporter;
        private String reporterPhone;
        private String reporterUnit;
        private String reporterDept;
        private String charger;
        private String chargerPhone;
        private String chargerUnit;
        private String chargerDept;
        private String incidentReasons;
        private String incidentMeasures;
        private String incidentDetails;
        private String groupName;
        private String sensitiveFlag;
        private String incidentProblems;
        private String incidentProgress;
        private String burnedArea;
        private String personId;
        private String longtitude;
        private String latitude;
        private String putoutTime;
        private String fireReason;
        private String serialNumber;
        private String topFlag;
        private String incidentLevel;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        public String getGroupNo() {
            return groupNo;
        }

        public void setGroupNo(String groupNo) {
            this.groupNo = groupNo;
        }

        public String getDeptNo() {
            return deptNo;
        }

        public void setDeptNo(String deptNo) {
            this.deptNo = deptNo;
        }

        public String getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(String delFlag) {
            this.delFlag = delFlag;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIncidentId() {
            return incidentId;
        }

        public void setIncidentId(String incidentId) {
            this.incidentId = incidentId;
        }

        public String getReportingStatus() {
            return reportingStatus;
        }

        public void setReportingStatus(String reportingStatus) {
            this.reportingStatus = reportingStatus;
        }

        public String getFallBackReason() {
            return fallBackReason;
        }

        public void setFallBackReason(String fallBackReason) {
            this.fallBackReason = fallBackReason;
        }

        public String getIncidentVersion() {
            return incidentVersion;
        }

        public void setIncidentVersion(String incidentVersion) {
            this.incidentVersion = incidentVersion;
        }

        public String getIncidentAddr() {
            return incidentAddr;
        }

        public void setIncidentAddr(String incidentAddr) {
            this.incidentAddr = incidentAddr;
        }

        public String getReportingTime() {
            return reportingTime;
        }

        public void setReportingTime(String reportingTime) {
            this.reportingTime = reportingTime;
        }

        public String getPropertyLoss() {
            return propertyLoss;
        }

        public void setPropertyLoss(String propertyLoss) {
            this.propertyLoss = propertyLoss;
        }

        public String getSensitive() {
            return sensitive;
        }

        public void setSensitive(String sensitive) {
            this.sensitive = sensitive;
        }

        public String getDeath() {
            return death;
        }

        public void setDeath(String death) {
            this.death = death;
        }

        public String getInjured() {
            return injured;
        }

        public void setInjured(String injured) {
            this.injured = injured;
        }

        public String getSeriousInjured() {
            return seriousInjured;
        }

        public void setSeriousInjured(String seriousInjured) {
            this.seriousInjured = seriousInjured;
        }

        public String getMissing() {
            return missing;
        }

        public void setMissing(String missing) {
            this.missing = missing;
        }

        public String getReporter() {
            return reporter;
        }

        public void setReporter(String reporter) {
            this.reporter = reporter;
        }

        public String getReporterPhone() {
            return reporterPhone;
        }

        public void setReporterPhone(String reporterPhone) {
            this.reporterPhone = reporterPhone;
        }

        public String getReporterUnit() {
            return reporterUnit;
        }

        public void setReporterUnit(String reporterUnit) {
            this.reporterUnit = reporterUnit;
        }

        public String getReporterDept() {
            return reporterDept;
        }

        public void setReporterDept(String reporterDept) {
            this.reporterDept = reporterDept;
        }

        public String getCharger() {
            return charger;
        }

        public void setCharger(String charger) {
            this.charger = charger;
        }

        public String getChargerPhone() {
            return chargerPhone;
        }

        public void setChargerPhone(String chargerPhone) {
            this.chargerPhone = chargerPhone;
        }

        public String getChargerUnit() {
            return chargerUnit;
        }

        public void setChargerUnit(String chargerUnit) {
            this.chargerUnit = chargerUnit;
        }

        public String getChargerDept() {
            return chargerDept;
        }

        public void setChargerDept(String chargerDept) {
            this.chargerDept = chargerDept;
        }

        public String getIncidentReasons() {
            return incidentReasons;
        }

        public void setIncidentReasons(String incidentReasons) {
            this.incidentReasons = incidentReasons;
        }

        public String getIncidentMeasures() {
            return incidentMeasures;
        }

        public void setIncidentMeasures(String incidentMeasures) {
            this.incidentMeasures = incidentMeasures;
        }

        public String getIncidentDetails() {
            return incidentDetails;
        }

        public void setIncidentDetails(String incidentDetails) {
            this.incidentDetails = incidentDetails;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getSensitiveFlag() {
            return sensitiveFlag;
        }

        public void setSensitiveFlag(String sensitiveFlag) {
            this.sensitiveFlag = sensitiveFlag;
        }

        public String getIncidentProblems() {
            return incidentProblems;
        }

        public void setIncidentProblems(String incidentProblems) {
            this.incidentProblems = incidentProblems;
        }

        public String getIncidentProgress() {
            return incidentProgress;
        }

        public void setIncidentProgress(String incidentProgress) {
            this.incidentProgress = incidentProgress;
        }

        public String getBurnedArea() {
            return burnedArea;
        }

        public void setBurnedArea(String burnedArea) {
            this.burnedArea = burnedArea;
        }

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getLongtitude() {
            return longtitude;
        }

        public void setLongtitude(String longtitude) {
            this.longtitude = longtitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getPutoutTime() {
            return putoutTime;
        }

        public void setPutoutTime(String putoutTime) {
            this.putoutTime = putoutTime;
        }

        public String getFireReason() {
            return fireReason;
        }

        public void setFireReason(String fireReason) {
            this.fireReason = fireReason;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getTopFlag() {
            return topFlag;
        }

        public void setTopFlag(String topFlag) {
            this.topFlag = topFlag;
        }

        public String getIncidentLevel() {
            return incidentLevel;
        }

        public void setIncidentLevel(String incidentLevel) {
            this.incidentLevel = incidentLevel;
        }
    }
}
