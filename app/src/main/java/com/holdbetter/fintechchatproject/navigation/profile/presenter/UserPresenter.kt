package com.holdbetter.fintechchatproject.navigation.profile.presenter

//class UserPresenter(
//    private val userId: Int,
//    private val chatRepository: IChatRepository? = null,
//    private val userViewer: IUserViewer,
//) : IUserPresenter {
//    private val compositeDisposable = CompositeDisposable()
//
//    private val idsForRandom = arrayOf(userId, -1)
//
//    override fun getUserById(userId: Int): Single<User> {
////        userViewer.startShimming()
////        val id = getRandomId()
////        return Single.fromCallable { chatRepository.users.find { it.id == id }!! }
////            .delay(3000, TimeUnit.MILLISECONDS)
////            .subscribeOn(Schedulers.io())
////            .doOnError { Thread.sleep(1000) }
////            .observeOn(AndroidSchedulers.mainThread())
////            .doOnSuccess { userViewer.stopShimming() }
//    }
//
//    override fun getStatus(isOnline: Boolean) = if (isOnline) "online" else "offline"
//
//    override fun bind() {
//        getUserById(userId)
//            .subscribeBy(
//                onSuccess = ::setUser,
//                onError = userViewer::handleError
//            ).addTo(compositeDisposable)
//    }
//
//    override fun unbind() {
//        compositeDisposable.clear()
//    }
//
//    private fun setUser(user: User) {
//        userViewer.setImage(user.avatarUrl)
//        userViewer.setUserName(user.name)
//        userViewer.setStatus(user.isOnline, getStatus(user.isOnline))
//    }
//
//    private fun getRandomId(): Int {
//        val id = Random.nextInt(0, 2)
//        return idsForRandom[id]
//    }
//}