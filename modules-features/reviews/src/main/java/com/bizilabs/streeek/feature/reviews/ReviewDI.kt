package com.bizilabs.streeek.feature.reviews

import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import org.koin.dsl.module

val ReviewModule =
    module {
        single<ReviewManager> {
            // use FakeReviewManager for Debug
            if (BuildConfig.DEBUG) {
                FakeReviewManager(get())
            } else {
                ReviewManagerFactory.create(get())
            }
        }
        single { ReviewManagerHelper(get()) }
    }
