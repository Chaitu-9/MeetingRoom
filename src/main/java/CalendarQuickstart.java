import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.http.HttpTransport;

import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.client.json.JsonFactory;

import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.client.util.DateTime;



import com.google.api.services.calendar.CalendarScopes;

import com.google.api.services.calendar.model.*;



import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.Arrays;

import java.util.List;

import java.util.Scanner;

import java.util.Calendar;

import java.util.Date;

import java.util.GregorianCalendar;

import java.util.TimeZone;



public class CalendarQuickstart {

    private static final String APPLICATION_NAME =  "Google Calendar API Java";

    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;



    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);



    static {

        try {

            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

        } catch (Throwable t) {

            t.printStackTrace();

            System.exit(1);

        }

    }



    public static Credential authorize() throws IOException {

        // Load client secrets.

        InputStream in = CalendarQuickstart.class.getResourceAsStream("/client_secret.json");

        GoogleClientSecrets clientSecrets =

                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));


        GoogleAuthorizationCodeFlow flow =

                new GoogleAuthorizationCodeFlow.Builder(

                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)

                        .setDataStoreFactory(DATA_STORE_FACTORY)

                        .setAccessType("offline")

                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(

                flow, new LocalServerReceiver()).authorize("user");

        System.out.println(

                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        return credential;

    }



    public static com.google.api.services.calendar.Calendar

    getCalendarService() throws IOException {

        Credential credential = authorize();

        return new com.google.api.services.calendar.Calendar.Builder(

                HTTP_TRANSPORT, JSON_FACTORY, credential)

                .setApplicationName(APPLICATION_NAME)

                .build();

    }



    public static void main(String[] args) throws IOException {

        com.google.api.services.calendar.Calendar service =

                getCalendarService();



        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = service.events().list("primary")

                .setMaxResults(10)

                .setTimeMin(now)

                .setOrderBy("startTime")

                .setSingleEvents(true)

                .execute();

        List<Event> items = events.getItems();

        if (items.size() == 0) {

            System.out.println("No upcoming events found.");

        } else {

            System.out.println("Upcoming events");

            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();

                if (start == null) {

                    start = event.getStart().getDate();

                }

                System.out.printf("%s (%s)\n", event.getSummary(), start);

            }

        }







        Scanner input = new Scanner(System.in);

        System.out.println("------Enter the name of the meeting-------");

        String meetingName = input.next();

        System.out.println("------Enter the duration of the meeting in minutes------");

        int meetingDuration = input.nextInt();



        Event event = new Event()

                .setSummary(meetingName)

                .setDescription("Instant meeting");



        Date startDate = new Date();

        Date endDate = new Date(startDate.getTime() + 60000*meetingDuration);

        DateTime startDateTime = new DateTime(startDate);

        EventDateTime start = new EventDateTime()

                .setDateTime(startDateTime)

                .setTimeZone("Asia/Calcutta");

        event.setStart(start);



        DateTime endDateTime = new DateTime(endDate);

        EventDateTime end = new EventDateTime()

                .setDateTime(endDateTime)

                .setTimeZone("Asia/Calcutta");

        event.setEnd(end);





        EventAttendee[] attendees = new EventAttendee[] {

                new EventAttendee().setEmail("prasanas@thoughtworks.com"),

                new EventAttendee().setEmail("thoughtworks.com_35333739303439372d363934@resource.calendar.google.com")

        };

        event.setAttendees(Arrays.asList(attendees));



        EventReminder[] reminderOverrides = new EventReminder[] {

                new EventReminder().setMethod("email").setMinutes(10),

                new EventReminder().setMethod("popup").setMinutes(10),

        };



        Event.Reminders reminders = new Event.Reminders()

                .setUseDefault(false)

                .setOverrides(Arrays.asList(reminderOverrides));

        event.setReminders(reminders);



        String calendarId = "primary";

        event = service.events().insert(calendarId, event).execute();

        System.out.printf("Event created: %s\n", event.getHtmlLink());



    }



}

