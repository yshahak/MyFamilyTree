package il.co.yshahak.familytree.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

import il.co.yshahak.familytree.R;
import il.co.yshahak.familytree.datamodel.Person;
import il.co.yshahak.familytree.datamodel.TreeManager;

import static il.co.yshahak.familytree.datamodel.Relationship.VIEW_TYPE_HERITAGE;
import static il.co.yshahak.familytree.datamodel.Relationship.VIEW_TYPE_MARRIAGE;
import static il.co.yshahak.familytree.datamodel.Relationship.VIEW_TYPE_ROOT;

public class PersonFormActivity extends AppCompatActivity {
    public static final int CODE_PERSON_FORM_EDIT = 100;
    public static final int CODE_PERSON_FORM_MARRIAGE = 101;
    public static final int CODE_PERSON_FORM_HERITAGE = 102;

    public static final String EXTRE_DEPTH = "extraDepth";
    public static final String EXTRE_VIEW_TYPE = "extreViewType";
    public static final String EXTRE_PERSON_NAME = "extrePersonName";
    public static final String EXTRE_PERSON_BD = "extraPersonBD";
    public static final String EXTRE_PERSON_ID = "extraId";
    public static final String EXTRE_GENDER = "extraGender";

    public static final int EXTRE_MALE = 0;
    public static final int EXTRE_FEMALE = 1;

    public static Person existPerson, secondExistsPerson;


    private EditText formName, formId;
    private RadioGroup radioGroupGender;
    private DatePicker datePicker;
    private int viewType, depth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_person);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        formName = (EditText)findViewById(R.id.person_name);
        formId = (EditText)findViewById(R.id.person_id);
        radioGroupGender = (RadioGroup)findViewById(R.id.radio_gender);
        depth = getIntent().getIntExtra(EXTRE_DEPTH, -1);
        viewType = getIntent().getIntExtra(EXTRE_VIEW_TYPE, -1);
        int gender = getIntent().getIntExtra(EXTRE_GENDER, -1);
        switch (gender){
            case EXTRE_MALE:
                radioGroupGender.findViewById(R.id.radio_female).setEnabled(false);
                radioGroupGender.findViewById(R.id.radio_male).setEnabled(false);
                break;
            case EXTRE_FEMALE:
                radioGroupGender.check(R.id.radio_female);
                radioGroupGender.findViewById(R.id.radio_female).setEnabled(false);
                radioGroupGender.findViewById(R.id.radio_male).setEnabled(false);
                break;
        }
        String name = getIntent().getStringExtra(EXTRE_PERSON_NAME);
        if (name != null) {
            formName.setText(name);
        }
        long bd = getIntent().getLongExtra(EXTRE_PERSON_BD, -1);
        if (bd > -1){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(bd);
        }
        long id = getIntent().getLongExtra(EXTRE_PERSON_ID, -1);
        if (id > -1){
            formId.setText(Long.toString(id));
            formId.setEnabled(false);
        }
    }

    public void save(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        long bd = calendar.getTimeInMillis();
        String name = formName.getText().toString();
        String id = formId.getText().toString();
        if (name.length() == 0 || id.length() == 0){
            Toast.makeText(this, getString(R.string.form_invalid), Toast.LENGTH_LONG).show();
            return;
        }

        boolean isMale = radioGroupGender.getCheckedRadioButtonId() == R.id.radio_male;
        Person person = new Person();
        person.setPersonId(Long.valueOf(id));
        person.setBirthDay(bd);
        person.setMale(isMale);
        person.setName(name);
        person.setDepth(depth);
        switch (viewType){
            case CODE_PERSON_FORM_EDIT:
                TreeManager.editPersonToDB(this, person);
                break;
            case VIEW_TYPE_MARRIAGE:
                TreeManager.marry(this, person, existPerson);
                break;
            case VIEW_TYPE_HERITAGE:
                TreeManager.addChildToParents(this, person, existPerson, secondExistsPerson);
                break;
            case VIEW_TYPE_ROOT:
                TreeManager.addPersonToDB(this, person);
                break;
        }
        setResult(RESULT_OK);
        finish();
    }
}
