package model;

import util.CsvUtil;

public class Patient {

    private String patientId;                 // patient_id
    private String firstName;                 // first_name
    private String lastName;                  // last_name
    private String dateOfBirth;               // date_of_birth
    private String nhsNumber;                 // nhs_number
    private String gender;                    // gender
    private String phoneNumber;               // phone_number
    private String email;                     // email
    private String address;                   // address
    private String postcode;                  // postcode
    private String emergencyContactName;      // emergency_contact_name
    private String emergencyContactPhone;     // emergency_contact_phone
    private String registrationDate;          // registration_date
    private String gpSurgeryId;               // gp_surgery_id

    public Patient(String patientId, String firstName, String lastName,
                   String dateOfBirth, String nhsNumber, String gender,
                   String phoneNumber, String email, String address, String postcode,
                   String emergencyContactName, String emergencyContactPhone,
                   String registrationDate, String gpSurgeryId) {

        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhsNumber = nhsNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.registrationDate = registrationDate;
        this.gpSurgeryId = gpSurgeryId;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNhsNumber() { return nhsNumber; }
    public void setNhsNumber(String nhsNumber) { this.nhsNumber = nhsNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getGpSurgeryId() { return gpSurgeryId; }
    public void setGpSurgeryId(String gpSurgeryId) { this.gpSurgeryId = gpSurgeryId; }


    public String toCsvRow() {
        return CsvUtil.toCsvRow(
                patientId,
                firstName,
                lastName,
                dateOfBirth,
                nhsNumber,
                gender,
                phoneNumber,
                email,
                address,
                postcode,
                emergencyContactName,
                emergencyContactPhone,
                registrationDate,
                gpSurgeryId
        );
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + patientId + ")";
    }
}
