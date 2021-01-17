package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidService
import com.udacity.asteroidradar.asteroidlist.AsteroidClickListener
import com.udacity.asteroidradar.asteroidlist.AsteroidListAdapter
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(requireActivity().application as AsteroidApplication)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = AsteroidListAdapter(AsteroidClickListener { asteroid ->
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })
        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.weekly_asteroids -> viewModel.setAsteroidFilter(AsteroidService.AsteroidsFilter.WEEKLY)
            R.id.today_asteroids -> viewModel.setAsteroidFilter(AsteroidService.AsteroidsFilter.TODAY)
            else -> viewModel.setAsteroidFilter(AsteroidService.AsteroidsFilter.SAVED)
        }
        return true
    }
}
