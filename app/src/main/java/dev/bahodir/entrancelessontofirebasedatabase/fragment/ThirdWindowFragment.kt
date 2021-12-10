package dev.bahodir.entrancelessontofirebasedatabase.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.bahodir.entrancelessontofirebasedatabase.R
import dev.bahodir.entrancelessontofirebasedatabase.databinding.FragmentThirdWindowBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "number"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdWindowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdWindowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var number: String? = null
    //private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            number = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }
    private var _binding: FragmentThirdWindowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentThirdWindowBinding.inflate(inflater, container, false)

        binding.phoneNumber.text = number

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param number Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdWindowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(number: String) =
            ThirdWindowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, number)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}