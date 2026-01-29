package com.arielaviv.studentsapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arielaviv.studentsapp.model.Student
import com.arielaviv.studentsapp.model.StudentRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class NewStudentFragment : Fragment() {

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutId: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextId: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var editTextBirthDate: TextInputEditText
    private lateinit var editTextBirthTime: TextInputEditText
    private lateinit var checkBoxChecked: CheckBox
    private lateinit var buttonCancel: Button
    private lateinit var buttonSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
    }

    private fun initViews(view: View) {
        textInputLayoutName = view.findViewById(R.id.textInputLayoutName)
        textInputLayoutId = view.findViewById(R.id.textInputLayoutId)
        editTextName = view.findViewById(R.id.editTextName)
        editTextId = view.findViewById(R.id.editTextId)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextAddress = view.findViewById(R.id.editTextAddress)
        editTextBirthDate = view.findViewById(R.id.editTextBirthDate)
        editTextBirthTime = view.findViewById(R.id.editTextBirthTime)
        checkBoxChecked = view.findViewById(R.id.checkBoxChecked)
        buttonCancel = view.findViewById(R.id.buttonCancel)
        buttonSave = view.findViewById(R.id.buttonSave)
    }

    private fun setupListeners() {
        buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        buttonSave.setOnClickListener {
            if (validateInput()) {
                saveStudent()
            }
        }

        editTextBirthDate.setOnClickListener {
            showDatePicker()
        }

        editTextBirthTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                editTextBirthDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()

        TimePickerDialog(
            requireContext(),
            { _, h, m ->
                editTextBirthTime.setText(String.format("%02d:%02d", h, m))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun validateInput(): Boolean {
        var isValid = true

        val name = editTextName.text.toString().trim()
        val id = editTextId.text.toString().trim()

        if (name.isEmpty()) {
            textInputLayoutName.error = "Name is required"
            isValid = false
        } else {
            textInputLayoutName.error = null
        }

        if (id.isEmpty()) {
            textInputLayoutId.error = getString(R.string.error_empty_id)
            isValid = false
        } else if (StudentRepository.exists(id)) {
            textInputLayoutId.error = getString(R.string.error_duplicate_id)
            isValid = false
        } else {
            textInputLayoutId.error = null
        }

        return isValid
    }

    private fun saveStudent() {
        val student = Student(
            id = editTextId.text.toString().trim(),
            name = editTextName.text.toString().trim(),
            phone = editTextPhone.text.toString().trim(),
            address = editTextAddress.text.toString().trim(),
            isChecked = checkBoxChecked.isChecked,
            birthDate = editTextBirthDate.text.toString(),
            birthTime = editTextBirthTime.text.toString()
        )

        StudentRepository.add(student)
        Toast.makeText(requireContext(), "Student added", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}
