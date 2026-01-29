package com.arielaviv.studentsapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arielaviv.studentsapp.model.Student
import com.arielaviv.studentsapp.model.StudentRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class EditStudentFragment : Fragment() {

    private lateinit var imageViewAvatar: ImageView
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextId: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var editTextBirthDate: TextInputEditText
    private lateinit var editTextBirthTime: TextInputEditText
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var idInputLayout: TextInputLayout
    private lateinit var checkBoxChecked: CheckBox
    private lateinit var buttonCancel: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonSave: Button

    private var originalStudentId: String? = null
    private var originalStudent: Student? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originalStudentId = arguments?.getString("studentId")

        // bind views
        imageViewAvatar = view.findViewById(R.id.imageViewAvatar)
        nameInputLayout = view.findViewById(R.id.textInputLayoutName)
        idInputLayout = view.findViewById(R.id.textInputLayoutId)
        editTextName = view.findViewById(R.id.editTextName)
        editTextId = view.findViewById(R.id.editTextId)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextAddress = view.findViewById(R.id.editTextAddress)
        editTextBirthDate = view.findViewById(R.id.editTextBirthDate)
        editTextBirthTime = view.findViewById(R.id.editTextBirthTime)
        checkBoxChecked = view.findViewById(R.id.checkBoxChecked)
        buttonCancel = view.findViewById(R.id.buttonCancel)
        buttonDelete = view.findViewById(R.id.buttonDelete)
        buttonSave = view.findViewById(R.id.buttonSave)

        loadStudentData()
        setupListeners()
    }

    private fun loadStudentData() {
        originalStudent = originalStudentId?.let { StudentRepository.getById(it) }

        if (originalStudent == null) {
            findNavController().popBackStack()
            return
        }

        val student = originalStudent!!
        imageViewAvatar.setImageResource(student.avatarResId)
        editTextName.setText(student.name)
        editTextId.setText(student.id)
        editTextPhone.setText(student.phone)
        editTextAddress.setText(student.address)
        editTextBirthDate.setText(student.birthDate)
        editTextBirthTime.setText(student.birthTime)
        checkBoxChecked.isChecked = student.isChecked
    }

    private fun setupListeners() {
        buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        buttonSave.setOnClickListener {
            if (validateInput()) {
                saveStudent()
            }
        }

        editTextBirthDate.setOnClickListener { showDatePicker() }
        editTextBirthTime.setOnClickListener { showTimePicker() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val existingDate = editTextBirthDate.text.toString()
        if (existingDate.isNotEmpty()) {
            val parts = existingDate.split("/")
            if (parts.size == 3) {
                calendar.set(Calendar.DAY_OF_MONTH, parts[0].toIntOrNull() ?: 1)
                calendar.set(Calendar.MONTH, (parts[1].toIntOrNull() ?: 1) - 1)
                calendar.set(Calendar.YEAR, parts[2].toIntOrNull() ?: calendar.get(Calendar.YEAR))
            }
        }

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                editTextBirthDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        // parse existing time if present
        val existingTime = editTextBirthTime.text.toString()
        if (existingTime.isNotEmpty()) {
            val parts = existingTime.split(":")
            if (parts.size == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 12)
                calendar.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
            }
        }

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                editTextBirthTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun validateInput(): Boolean {
        val name = editTextName.text.toString().trim()
        val id = editTextId.text.toString().trim()

        if (name.isEmpty()) {
            nameInputLayout.error = getString(R.string.error_empty_name)
            return false
        }
        nameInputLayout.error = null

        if (id.isEmpty()) {
            idInputLayout.error = getString(R.string.error_empty_id)
            return false
        }
        if (id != originalStudentId && StudentRepository.exists(id)) {
            idInputLayout.error = getString(R.string.error_duplicate_id)
            return false
        }
        idInputLayout.error = null

        return true
    }

    private fun saveStudent() {
        val updatedStudent = Student(
            id = editTextId.text.toString().trim(),
            name = editTextName.text.toString().trim(),
            phone = editTextPhone.text.toString().trim(),
            address = editTextAddress.text.toString().trim(),
            isChecked = checkBoxChecked.isChecked,
            birthDate = editTextBirthDate.text.toString().trim(),
            birthTime = editTextBirthTime.text.toString().trim(),
            avatarResId = originalStudent!!.avatarResId
        )

        StudentRepository.update(originalStudentId!!, updatedStudent)

        Toast.makeText(requireContext(), "Student updated", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete student?")
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton("Delete") { _, _ ->
                StudentRepository.delete(originalStudentId!!)
                // TODO: maybe add undo with snackbar
                findNavController().popBackStack(R.id.studentsListFragment, false)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
