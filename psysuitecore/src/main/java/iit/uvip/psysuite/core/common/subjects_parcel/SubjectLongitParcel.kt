package iit.uvip.psysuite.core.common.subjects_parcel

import android.content.Context
import android.os.Parcel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import iit.uvip.psysuite.core.common.TestBasic
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import org.albaspazio.core.accessory.existFile
import org.albaspazio.core.accessory.readText
import org.albaspazio.core.accessory.saveText

// base class for all longitudinal tests
@Parcelize
open class SubjectLongitParcel(
    override var type: Int = -1,
    override var label: String = "",
    override var age: Int = -1,
    override var gender: Int = -1,
    override var nextTrailModality: Int = -1,
    override var canRecordAudio:Boolean = false,
    override var testClass:String = "",
    override var spinner_sel: Int = -1,
    override var spinner_data_resource: Int = -1
) : SubjectBasicListParcel(type, label, age, gender, nextTrailModality, canRecordAudio, testClass,
    spinner_sel,
    "session",
    spinner_data_resource
) {

    var session: Int
        get() = spinner_sel
        set(value) {
            spinner_sel = value
        }

    var test_sessions_array: Int
        get() = spinner_data_resource
        set(value) {
            spinner_data_resource = value
        }

    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() > 0,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    )


    companion object : Parceler<SubjectLongitParcel> {

        override fun SubjectLongitParcel.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(type)
            parcel.writeString(label)
            parcel.writeInt(age)
            parcel.writeInt(gender)
            parcel.writeInt(nextTrailModality)
            if (canRecordAudio)                 parcel.writeInt(1)
            else                                parcel.writeInt(0)
            parcel.writeString(testClass)
            parcel.writeInt(spinner_sel)
            parcel.writeInt(spinner_data_resource)
        }

        override fun create(parcel: Parcel) = SubjectLongitParcel(parcel)

        private fun loadJsonText(jsontext:String): SubjectLongitParcel {
            val moshi           = Moshi.Builder().build()
            val jsonAdapter     = moshi.adapter(SubjectLongitParcel::class.java)
            return jsonAdapter.fromJson(jsontext)!!
        }

        fun loadSubject(): SubjectLongitParcel{
            val subj = existFile(CURR_SUBJ_FILE + TestBasic.FILE_EXTENSION)
            if(subj.first){
                val jsontext = readText(CURR_SUBJ_FILE + TestBasic.FILE_EXTENSION)
                return try {
                    loadJsonText(jsontext)
                }
                catch (e:Exception){
                    SubjectLongitParcel()
                }
            }
            return SubjectLongitParcel()
        }
    }

    // =============================================================================================================
    // WRITE
    // =============================================================================================================
    override fun writeJson(context: Context, filename:String){

        val moshi       = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(SubjectLongitParcel::class.java)

        return try {
            val json_subject = jsonAdapter.toJson(this)
            saveText(context, filename + TestBasic.FILE_EXTENSION, json_subject)        // var jsontext = context!!.resources.openRawResource(R.raw.script_001).bufferedReader().use { it.readText() }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            return
        }
    }
}












