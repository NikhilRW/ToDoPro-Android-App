package com.example.to_do_list.Frontend.attachments

import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.to_do_list.Frontend.Acitivities.AddTaskActivity
import com.example.to_do_list.Frontend.Acitivities.EditTaskActivity
import com.example.to_do_list.R
import com.nareshchocha.filepickerlibrary.FilePickerResultContracts
import com.nareshchocha.filepickerlibrary.models.BaseConfig
import com.nareshchocha.filepickerlibrary.models.DocumentFilePickerConfig
import com.nareshchocha.filepickerlibrary.models.FilePickerResult


class Attachment()
{
    var launcher: ActivityResultLauncher<BaseConfig?>? = null;
    var activity:AppCompatActivity? = null;
    val config = DocumentFilePickerConfig(
        popUpText = "File Media",
        allowMultiple = false,
        maxFiles = 1,
        mMimeTypes = listOf("application/pdf", "image/*")
    )
    constructor(myActivity: AppCompatActivity,isAdd:Boolean) : this() {
        this.activity = myActivity;
        if(isAdd){
            this.launcher = activity?.registerForActivityResult(FilePickerResultContracts.AnyFilePicker()) { result ->
                getResultCallback(result,activity as AddTaskActivity,null);
            }
        }
        else{
            launcher = activity?.registerForActivityResult(FilePickerResultContracts.AnyFilePicker()) { result ->
                getResultCallback(result,null,activity as EditTaskActivity);
            }
        }
    }

    fun setupButton(){
        val button = activity?.findViewById<ImageButton>(R.id.upload_btn);
        button?.setOnClickListener { l ->
            // Actually File Picking Using a Library.
            launchUploadActivity();
        }
    }
    fun removeListenerFromButton(){
        val button = activity?.findViewById<ImageButton>(R.id.upload_btn);
        button?.setOnClickListener {};
    }

    fun launchUploadActivity(){
        launcher?.launch(config)
    }

    fun getResultCallback(result: FilePickerResult,addActivity: AddTaskActivity?,editActivity: EditTaskActivity?){
        if (result.errorMessage != null) {
            Log.e("Picker", result.errorMessage ?: "")
        } else {
            var filePath =result.selectedFilePath;
            if(addActivity != null){
                addActivity.attachmentFilePath = filePath;
                addActivity.currentAttachment.text = filePath?.substring(filePath.lastIndexOf("/")
                    .plus(1));
            }
            else if(editActivity != null){
                editActivity.attachmentFilePath = filePath;
                editActivity.currentTask.attachment = filePath;
                editActivity.currentAttachment.text= filePath?.substring(filePath.lastIndexOf("/")
                    .plus(1));
            }
        }
    }
}





// - Old Code -
//fun getFilepath(intent:Intent){
//      var contentUri: Uri? = intent.data;
//      contentUri?.let {
//          var inputStream: InputStream? = activity?.contentResolver?.openInputStream(contentUri)
//            var filename:String? = contentUri.path?.substring(contentUri.path?.lastIndexOf("/")
//                ?.plus(1) ?: 0);
//        var file:File? = File(activity?.getApplicationContext()?.getFilesDir()?.getAbsolutePath()+"/"+filename);
//        file?.createNewFile();
//        file?.setWritable(true);
//        var outputStream = file?.outputStream();
//        var currentByte:Int? =0;
//        while (inputStream?.read() != -1){
//            currentByte = inputStream?.read();
//            currentByte?.let {
//                    outputStream?.write(currentByte);
//                }
//            }
//            activity?.attachmentFilePath = file?.absolutePath;
//        }
//    }
