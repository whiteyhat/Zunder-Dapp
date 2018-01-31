package android.ebs.zunderapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton inbox, locations, calendar, contacts, notifications,
                        like, views, item1, item2, item3, carSharing;
    private ImageView home, wallet, store, map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate XML elements to Java class
        instantiateElements();

        //Link the buttons to the method OnClick()
        addListening();
    }

    /**
     * Method that creates a link between the front end design
     * of the XML layout and its element with the logic Java class
     */
    private void instantiateElements(){
        inbox = (ImageButton) findViewById(R.id.MessageButton);
        locations = (ImageButton) findViewById(R.id.LocationButton);
        calendar = (ImageButton) findViewById(R.id.CalendarButton);
        contacts = (ImageButton) findViewById(R.id.ContactsButton);
        notifications = (ImageButton) findViewById(R.id.NotificationsButton);
        like = (ImageButton) findViewById(R.id.LikeButton);
        views = (ImageButton) findViewById(R.id.ViewsButton);
        item1 = (ImageButton) findViewById(R.id.EcoLockButton);
        item2 = (ImageButton) findViewById(R.id.SmartContractButton);
        item3 = (ImageButton) findViewById(R.id.GuardAInButton);
        carSharing = (ImageButton) findViewById(R.id.CarSharingButton);

        home = (ImageView) findViewById(R.id.Home);
        wallet = (ImageView) findViewById(R.id.Wallet);
        store = (ImageView) findViewById(R.id.Store);
        map = (ImageView) findViewById(R.id.Map);

    }

    /**
     * Method that creates an action listener to the
     * buttons when the user touch each button it is forwarded
     * to the onClick() function
     */
    private void addListening(){
        inbox.setOnClickListener(this);
        locations.setOnClickListener(this);
        calendar.setOnClickListener(this);
        contacts.setOnClickListener(this);
        notifications.setOnClickListener(this);
        like.setOnClickListener(this);
        views.setOnClickListener(this);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        carSharing.setOnClickListener(this);
        home.setOnClickListener(this);
        wallet.setOnClickListener(this);
        store.setOnClickListener(this);
        map.setOnClickListener(this);
    }

    /**
     * Method to create a new event/activity when the
     * user touch any button
     * @param view get the user touch
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MessageButton:

                break;
        }
    }
}
