package skylight.com.applicationthemedemo.fragment;

/**
 * Created by Akash on 24/01/16.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import skylight.com.applicationthemedemo.R;

/**
 * Created by Akash on 14/01/16.
 */
public class ChooseThemeFragment extends DialogFragment {

    private String[] mChoiceCode;
    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    ChooseListener chooseCodeListener;

    public ChooseThemeFragment() {
        //empty constuctor required as per Android documentation
    }

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface ChooseListener {
        public void onPositiveClick(int position, String code);
        public void onNegativeClick();
    }

    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            chooseCodeListener = (ChooseListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            String code = mChoiceCode[position];
            chooseCodeListener.onPositiveClick(position,code);
        }
    };

    DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            String code = mChoiceCode[position];
            chooseCodeListener.onNegativeClick();
        }
    };

    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        mChoiceCode = getResources().getStringArray(R.array.theme_options);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        b.setTitle(getString(R.string.select_theme));

        b.setSingleChoiceItems(mChoiceCode, position, null);

        b.setPositiveButton(getString(R.string.ok),positiveListener);

        b.setNegativeButton(getString(R.string.cancel), negativeListener);

        AlertDialog d = b.create();

        return d;
    }
}


