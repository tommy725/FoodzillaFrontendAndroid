package pl.better.foodzilla.data.models.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchSort(
    val attribute: String,
    val direction: SearchSortDirection
) : Parcelable