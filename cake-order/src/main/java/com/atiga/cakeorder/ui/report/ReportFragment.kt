package com.atiga.cakeorder.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atiga.cakeorder.core.domain.enum.ReportType
import com.atiga.cakeorder.databinding.ReportFragmentBinding
import com.atiga.cakeorder.ui.report.detail.DetailReportActivity
import com.atiga.cakeorder.ui.report.track.TrackOrderActivity
import org.koin.android.viewmodel.ext.android.viewModel

class ReportFragment : Fragment() {

    private val reportViewModel: ReportViewModel by viewModel()
    private var _binding: ReportFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReportFragmentBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.cardTrackOrder.setOnClickListener { startActivity(Intent(activity, TrackOrderActivity::class.java)) }

        binding.cardAllOrder.setOnClickListener { startActivity(Intent(activity, DetailReportActivity::class.java).putExtra("reportType", ReportType.ORDER.id)) }

        binding.cardUnfinishedOrder.setOnClickListener { startActivity(Intent(activity, DetailReportActivity::class.java).putExtra("reportType", ReportType.UNFINISHED_ORDER.id))}
        binding.cardFinishedOrder.setOnClickListener { startActivity(Intent(activity, DetailReportActivity::class.java).putExtra("reportType", ReportType.FINISHED_ORDER.id))}
        binding.cardPickedOrder.setOnClickListener { startActivity(Intent(activity, DetailReportActivity::class.java).putExtra("reportType", ReportType.PICKED_ORDER.id))}
    }
}