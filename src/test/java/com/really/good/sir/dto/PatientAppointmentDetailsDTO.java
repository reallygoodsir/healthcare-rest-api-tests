package com.really.good.sir.dto;

public class PatientAppointmentDetailsDTO {

    private int appointmentId;
    private String date;
    private String startTime;
    private String endTime;
    private String status;

    private int doctorId;
    private String doctorFirstName;
    private String doctorLastName;

    private int serviceId;
    private String serviceName;

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDoctorFirstName() { return doctorFirstName; }
    public void setDoctorFirstName(String doctorFirstName) { this.doctorFirstName = doctorFirstName; }

    public String getDoctorLastName() { return doctorLastName; }
    public void setDoctorLastName(String doctorLastName) { this.doctorLastName = doctorLastName; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
