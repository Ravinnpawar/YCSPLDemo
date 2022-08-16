package com.ravindra.ycspldemo.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ravindra.ycspldemo.data.models.PropertyDetails
import com.ravindra.ycspldemo.repositories.PropertyRepository

class MapViewModel : ViewModel() {
    var liveDataPropertyDetails: LiveData<PropertyDetails>? = null

    var liveDataPropertyDetailsList: LiveData<List<PropertyDetails>>? = null

    fun insertData(context: Context, propertyName: String, lattitude: Double,longitude:Double) {
        PropertyRepository.insertData(context, propertyName, lattitude,longitude)
    }

    fun getPropertyDetails(context: Context) : LiveData<List<PropertyDetails>>? {
        liveDataPropertyDetailsList = PropertyRepository.getPropertyDetails(context)
        return liveDataPropertyDetailsList
    }

}