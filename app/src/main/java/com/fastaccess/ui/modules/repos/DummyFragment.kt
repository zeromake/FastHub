package com.fastaccess.ui.modules.repos

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fastaccess.R

/**
 * Created by Kosh on 11 Mar 2017, 12:10 AM
 */
class DummyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.single_container_layout, container, false)
    }
}