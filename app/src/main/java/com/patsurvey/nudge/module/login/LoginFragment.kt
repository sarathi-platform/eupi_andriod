package com.patsurvey.nudge.module.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseFragment
import com.patsurvey.nudge.databinding.FragmentLoginBinding
import com.patsurvey.nudge.model.responseModel.LoginResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): LoginViewModel {
        return loginViewModel
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.userLoginResult.observe(viewLifecycleOwner) {
            if (it.data is LoginResponse) {
                Toast.makeText(context, it.data.email, Toast.LENGTH_SHORT).show()
                activity?.finish()
            } else {
                Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


}