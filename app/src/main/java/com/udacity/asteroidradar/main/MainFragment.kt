package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import timber.log.Timber

class MainFragment : Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)

    }

    private lateinit var asteroidAdapter:AsteroidListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        asteroidAdapter = AsteroidListAdapter(AsteroidClickListener {
            it.let {
                Timber.i("Asteroid is clicked####"+it.codename)
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            }
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(viewLifecycleOwner, Observer{
            Timber.i("Asteroid list has changed"+it?.size)
            it.apply {
                asteroidAdapter.submitList(this)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.optionsChanged( when (item.itemId) {
            R.id.show_rent_menu -> {
                OptionState.TODAY
            }
            R.id.show_all_menu -> {
                OptionState.WEEK
            }
            else -> {
                OptionState.ALL
            }
        })
        return true
    }
}
