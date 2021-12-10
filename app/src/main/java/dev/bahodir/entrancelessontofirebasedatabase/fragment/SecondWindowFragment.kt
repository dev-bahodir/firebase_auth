package dev.bahodir.entrancelessontofirebasedatabase.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dev.bahodir.entrancelessontofirebasedatabase.R
import dev.bahodir.entrancelessontofirebasedatabase.databinding.FragmentSecondWindowBinding
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "phone"
private const val ARG_PARAM2 = "number"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondWindowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondWindowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var phone: String? = null
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phone = it.getString(ARG_PARAM1)
            number = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentSecondWindowBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val TAG = "MainFirebaseActivity"
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondWindowBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        sendVerificationCode(phone)

        binding.tv2.text = "The one-time code was sent to $number"
        binding.timer.text = 60.startResendTimer().toString()

        binding.enter.setOnClickListener {
            reSendVerificationCode(phone)
        }

        binding.smsCode.addTextChangedListener {
            if (it?.length == 6) {
                verifyCode(it.toString())
                val bundle = Bundle()
                bundle.putString("number", number.toString())
                findNavController().navigate(R.id.action_secondWindowFragment_to_thirdWindowFragment,
                    bundle)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sendVerificationCode(phone)
    }


    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }


    private fun Int.startResendTimer() {
        binding.timer.visibility = View.VISIBLE
        binding.enter.isEnabled = false

        object : CountDownTimer((this * 1000).toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var secondsString = (millisUntilFinished / 1000).toString()
                if (millisUntilFinished < 600) {
                    secondsString = "0$secondsString"
                }
                binding.timer.text = " 0:$secondsString"
            }

            override fun onFinish() {
                binding.timer.visibility = View.GONE
                binding.enter.isEnabled = true
                binding.enter.setOnClickListener {
                    binding.timer.visibility = View.VISIBLE
                    60.startResendTimer().toString()
                }
            }
        }.start()
    }

    private fun reSendVerificationCode(phoneNumber: String?) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setForceResendingToken(resendToken)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendVerificationCode(phoneNumber: String?) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
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
         * @param phone Parameter 1.
         * @param number Parameter 2.
         * @return A new instance of fragment SecondWindowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(phone: String, number: String) =
            SecondWindowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, phone)
                    putString(ARG_PARAM2, number)
                }
            }
    }
}