import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.polary.PostView.AuthorAdapterSpinner
import com.example.polary.R
import com.example.polary.dataClass.Author

class AuthorSpinnerFragment : Fragment() {
    private lateinit var authorSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.author_spinner, container, false)

        // Initialize the authorSpinner
        authorSpinner = view.findViewById(R.id.author_spinner)

        return view
    }

    fun updateSpinnerData(users: List<Author>) {
        val adapter = AuthorAdapterSpinner(requireContext(), R.layout.author_spinner_item, users)
        adapter.setDropDownViewResource(R.layout.author_spinner_item)
        authorSpinner.adapter = adapter
    }
}