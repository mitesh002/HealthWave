package com.works.muhtas2.patient.services

import com.google.firebase.firestore.FirebaseFirestore
import com.works.muhtas2.patient.models.PatientAppointmentData

class PatientAppointmentService {
    private val db = FirebaseFirestore.getInstance()

    fun getAppointmentsForPatient(patientEmail: String, callback: (List<PatientAppointmentData>) -> Unit) {
        db.collection("appointments")
            .document(patientEmail)
            .collection("patientAppointments")
            .get()
            .addOnSuccessListener { documents ->
                val appointmentsList = documents.mapNotNull { document ->
                    val appointment = document.toObject(PatientAppointmentData::class.java)
                    appointment?.copy(id = document.id) // Add the document ID
                }
                callback(appointmentsList)
            }
    }
    fun deleteAppointment(patientEmail: String, doctorEmail: String, appointmentId: String, callback: (Boolean) -> Unit) {
        // "Delete from the patient collection."
        db.collection("appointments")
            .document(patientEmail)
            .collection("patientAppointments")
            .document(appointmentId)
            .delete()
            .addOnSuccessListener {
                // Delete from the doctor collection as well.
                db.collection("doctorAppointments")
                    .document(doctorEmail)
                    .collection("appointments")
                    .document(appointmentId)
                    .delete()
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}