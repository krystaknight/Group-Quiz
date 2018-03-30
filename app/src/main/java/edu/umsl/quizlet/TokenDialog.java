package edu.umsl.quizlet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by harprabh sangha on 3/2/2017
 * Quiz ID Dialog Written with the assistance from https://developer.android.com/guide/topics/ui/dialogs.html
 * OverRiding dialog.close on Start Quiz button Code taken from http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
 * This Dialog is called from ProfilePageActivity and it creates a DialogBox or the user to input Token and startQuiz
 * DialogType 2 is called from chose course page and it displays a DialogBox with user choosing a course and
 */

public class TokenDialog extends DialogFragment {

    private EditText mTokenInput; //EditText of Token input
    private String courseClicked; //Getting the course the User clicked

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        void getQuiz(String Token);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    //show takes the tag from dialog.show() and determines which type of dialog box to create by
    // modifying dialogType
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        courseClicked = tag;
    }

    // Creating Dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View inflatedView = View.inflate(getActivity(), R.layout.token_dialogue, null);
        //Inflating and setting the layout for the dialog
        //passing null as the parent view because its going in the dialog layout
        builder.setView(inflatedView)
                //Adding Action buttons and title to dialogs
                //The Title is Input Token located in the strings.xml + courseClicked gotten from profile acitvity
                .setTitle(courseClicked);

        // Setting the buttons ere because they will always need to be created in any dialog
        //Positive Button - Start Quiz
        builder.setPositiveButton(R.string.startquiz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Doing Nothing here because the PositiveButton getts overriden in OnStart() to change Close behaviour
                //This function is still needed for older versions of Android (Below API 8)
            }
        }); //Negative Button - Cancel (Closes Dialog Box)
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Closing the Dialog Box
                TokenDialog.this.getDialog().cancel();
            }
        });

        return builder.create(); //creating the dialogBox in accordance to the builder
    }

    // Using OnStart to overRide positiveButton in order to prvent the dialog from closing until input is verified
    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("TokenDialog", "Start Quiz Button Clicked");
                    //mListener.onDialogPositiveClick(dialog);
                    if (CheckTokenInput(dialog)) { //If input is verified then dialog closes
                        dialog.dismiss();
                        //Token Sent to get Quiz from server
                        mListener.getQuiz(mTokenInput.getEditableText().toString());
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    // This function checks
    private boolean CheckTokenInput(AlertDialog dialog) {

        mTokenInput = (EditText) dialog.findViewById(R.id.TokenInput);
        if (mTokenInput.getEditableText().toString().equals("")) {
            Toast toast = Toast.makeText(getContext(), "Please Input a Token", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }
}
