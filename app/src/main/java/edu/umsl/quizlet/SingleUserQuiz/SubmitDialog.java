package edu.umsl.quizlet.SingleUserQuiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.umsl.quizlet.R;

/**
 * Created by harprabh sangha on 3/2/2017
 * Quiz ID Dialog Written with the assistance from https://developer.android.com/guide/topics/ui/dialogs.html
 * OverRiding dialog.close on Start Quiz button Code taken from http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
 * This Dialog is called from ProfilePageActivity and it creates a DialogBox or the user to input Token and startQuiz
 * DialogType 2 is called from chose course page and it displays a DialogBox with user choosing a course and
 */

public class SubmitDialog extends DialogFragment {

    private EditText mTokenInput; //EditText of Token input
    private Spinner mChooseCourseSpinner; //Dropdown for the user to chose a course
    private int dialogType; //This allows me to have more than 1 type of dialog box

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void submitQuiz();
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    //show takes the tag from dialog.show() and determines which type of dialog box to create by
    // modifing dialogType
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        if (tag.equals("SubmitDialog")) { //checking tag crated when called dialog.show()
            dialogType = 0;
        } else {
            dialogType = 1;
        }
    }

    // Creating Dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder = createSubmitDialog(builder);
        if (dialogType == 0) {
            builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Closing the Dialog Box
                    SubmitDialog.this.getDialog().cancel();
                }
            });
        } else if (dialogType == 1) {
            builder.setCancelable(false);
        }

        // Setting the buttons ere because they will always need to be created in any dialog
        //Positive Button - Start Quiz
        builder.setPositiveButton(R.string.submitQuizDialogue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Doing Nothing here because the PositiveButton getts overriden in OnStart() to change Close behaviour
                //This function is still needed for older versions of Android (Below API 8)
            }
        }); //Negative Button - Cancel (Closes Dialog Box)
        return builder.create(); //creating the dialogbox in accordance to the builder
    }



    // Creating a dialog box that asks if the user is certain they want to submit the quiz
    private AlertDialog.Builder createSubmitDialog(AlertDialog.Builder builder) {
        View inflatedView = View.inflate(getActivity(), R.layout.fragment_dialog_single_user_quiz_submit,null);
        if (dialogType == 1) {
            ((TextView)inflatedView.findViewById(R.id.singleUserQuizSubmitVerificationTextView)).setText("Time has run out and you must now submit.");
        }
        //Inflating and setting the layout for the dialog
        //passing null as the parent view beacuse its going in the dialog layout
        builder.setView(inflatedView)
                //Adding Action buttons and title to dialogs
                //The Title is Choose Course located in the strings.xml
                .setTitle(R.string.submitDialogTitle);
        return builder;
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
                dialog.dismiss();
                //Token Sent to get Quiz from server
                mListener.submitQuiz();
                }
            });
        }
        if (dialogType == 1) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            dialog.setMessage("Time is up; quiz will be submitted.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            return true;
                        }
                        return false;
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
}
