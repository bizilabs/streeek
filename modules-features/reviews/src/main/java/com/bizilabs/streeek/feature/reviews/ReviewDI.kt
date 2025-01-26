package com.bizilabs.streeek.feature.reviews

import com.bizilabs.streeek.feature.reviews.presentation.InAppReviewViewModel
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ReviewModule =
    module {
        single { ReviewManagerFactory.create(get()) }
        single { ReviewManagerHelper(get()) }
        viewModel { InAppReviewViewModel(get()) }
    }
