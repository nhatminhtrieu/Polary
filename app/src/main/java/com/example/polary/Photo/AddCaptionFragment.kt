import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.example.polary.R
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText

class AddCaptionFragment(private var caption: String = "") : DialogFragment() {
    private var mOnInputListener: OnInputListener? = null
    interface OnInputListener {
        fun sendInput(input: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_caption, container, false)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Inflate the layout for this fragment
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_add_caption, null)
            val editText = view.findViewById<TextInputEditText>(R.id.add_caption)
            editText.requestFocus()
            Log.i("AddCaptionFragment", "onCreateDialog: caption is $caption")
            editText.setText(caption)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    caption = s.toString()
                }

                override fun afterTextChanged(s: Editable) {
                    caption = s.toString()
                }
            })
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(editText.windowToken, 0)
                    dismiss()
                    true
                } else {
                    false
                }
            }
            builder.setView(view)
            // Add any additional setup code here
            builder.create()
            // Add any additional setup code here
            val dialog = builder.create()
            // Set the background to transparent
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mOnInputListener = try {
            context as OnInputListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnInputListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        // Hide the keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow((dialog as Dialog).window?.decorView?.windowToken, 0)

        mOnInputListener?.sendInput(caption ?: "")
    }
}