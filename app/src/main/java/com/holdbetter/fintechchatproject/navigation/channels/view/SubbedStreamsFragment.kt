package com.holdbetter.fintechchatproject.navigation.channels.view

//class SubbedStreamsFragment : Fragment(R.layout.fragment_streams_sub_or_not),
//    IStreamFragment {
//    companion object {
//        private const val STREAMS_KEY = "streams"
//
//        fun newInstance(): SubbedStreamsFragment {
//            val bundle = Bundle()
//            return SubbedStreamsFragment().apply {
//                arguments = bundle
//            }
//        }
//    }
//
//    private var streamsList: RecyclerView? = null
//    private var shimmerContent: ListView? = null
//    var shimmer: ShimmerFrameLayout? = null
//
//    private val viewModel: StreamViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        streamsList = view.findViewById(R.id.streams_list)
//        shimmerContent = view.findViewById(R.id.shimmer_content)
//        shimmer = view.findViewById(R.id.shimmer)
//
//        shimmerContent!!.adapter = ShimmerPlaceholderStreamListAdapter(view.context)
//
//        streamsList!!.apply {
//            addItemDecoration(DividerItemDecoration(view.context,
//                DividerItemDecoration.VERTICAL).apply {
//                setDrawable(ContextCompat.getDrawable(view.context,
//                    R.drawable.streams_divider_decoration)!!)
//            })
//
//            layoutManager = object : LinearLayoutManager(this.context, VERTICAL, false) {
//                override fun supportsPredictiveItemAnimations(): Boolean {
//                    return false
//                }
//            }
//
////            adapter = StreamAdapter(viewModel)
//        }
//
////        viewModel.streamViewState.observe(viewLifecycleOwner, ::handleStreamViewState)
//    }
//
//    private fun handleError(e: Throwable) {
//        val appResource = resources
//        val appTheme = requireActivity().theme
//
//        val snackbar = Snackbar.make(requireView() , "Нет подключения к интернету", Snackbar.LENGTH_INDEFINITE).apply {
//            setActionTextColor(appResource.getColor(R.color.blue_and_green, appTheme))
//            setTextColor(appResource.getColor(android.R.color.black, appTheme))
//            setBackgroundTint(appResource.getColor(R.color.white, appTheme))
//
//            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).apply {
//                typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
//            }
//
//            setAction("Повторить") { viewModel.getStreams() }
//        }
//
//        when(e) {
//            is NotConnectedException, is IOException -> snackbar.show()
//        }
//    }
//}