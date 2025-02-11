package uk.org.pentlandscouts.events.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.exception.EventAttendeeException;
import uk.org.pentlandscouts.events.exception.EventAttendeeNotFoundException;
import uk.org.pentlandscouts.events.model.EventAttendee;
import uk.org.pentlandscouts.events.model.EventAttendeeHist;
import uk.org.pentlandscouts.events.service.EventAttendeeHistService;
import uk.org.pentlandscouts.events.service.EventAttendeeService;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/eventattendee")

public class EventAttendeeController {

    private static final Logger logger = LoggerFactory.getLogger(EventAttendeeController.class);

    @Autowired
    EventAttendeeService service;
    @Autowired
    EventAttendeeHistService histService;

    private static final String TABLE_NAME = "EventAttendee";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";



    /**
     * Find all EventAttendee Records
     *
     * @return all EventAttendee Records
     */
    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {

        Map<String, List<EventAttendee>> response = new HashMap<>(1);
        try {
            List<EventAttendee> eventAttendeeList = service.findAll();
            if (eventAttendeeList.isEmpty()) {
                return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, eventAttendeeList);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findbyevent")
    ResponseEntity<Object> findByEventUid(
            @RequestParam(value = "enventuid") String eventuid) {
        Map<String, List<EventAttendee>> response = new HashMap<>(1);
        try {
            if (!eventuid.isEmpty()) {

                List<EventAttendee> eventAttendeeList = service.findByEventUid(eventuid);
                if (eventAttendeeList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/findbyperson")
    ResponseEntity<Object> findByPersonUid(
            @RequestParam(value = "personuid") String personuid) {
        Map<String, List<EventAttendee>> response = new HashMap<>(1);
        try {
            if (!personuid.isEmpty()) {

                List<EventAttendee> eventAttendeeList = service.findByPersonUid(personuid);
                if (eventAttendeeList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/find")
    ResponseEntity<Object> findByEventUidAndCheckedIn(
            @RequestParam(value = "eventUid") String eventUid,
            @RequestParam(value = "checkedIn") String checkedIn) {
        Map<String, List<EventAttendee>> response = new HashMap<>(1);
        try {
            if (!eventUid.isEmpty()) {

                List<EventAttendee> eventAttendeeList = service.findByEventUidAndCheckedIn(eventUid,checkedIn);
                if (eventAttendeeList.isEmpty()) {
                    return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);
                }
                response.put(TABLE_NAME, eventAttendeeList);
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> createEventAttendee(@RequestBody EventAttendee eventAttendee) {
        try {

            EventAttendee result;
            //Lookup the DB for an existing record
            List<EventAttendee> lookUpEventAttendees = service.findByEventUidAndPersonUid(eventAttendee.getEventUid(),eventAttendee.getPersonUid());
            if (lookUpEventAttendees.size() == 0) {
                //Person not found create new record
                eventAttendee.setUid(EventUtils.generateType1UUID().toString());
                eventAttendee.setSortKey(eventAttendee.getEventUid() + eventAttendee.getPersonUid());
                logger.info("Creating new record: {}", eventAttendee);


                result = service.createRecord(eventAttendee);
            } else {
                result = lookUpEventAttendees.get(0);
            }

            Map<String, EventAttendee> response = new HashMap<>(1);
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public EventAttendee update(@RequestBody EventAttendee eventAttendee) {
        try {

            if (eventAttendee != null && !eventAttendee.getUid().isEmpty()) {
                //Find if the record exists
                List<EventAttendee> currentEvent = service.findByEventUidAndPersonUid(eventAttendee.getUid(),eventAttendee.getPersonUid());
                if (!currentEvent.isEmpty()) {
                    return service.update(eventAttendee);
                } else {

                    logger.info("EventAttendee not found in the db: {}. No updates", eventAttendee);
                    throw new EventAttendeeException("EventAttendee not found in the db: " + eventAttendee + ". No updates");
                }
            }
        } catch (EventAttendeeException ex) {
            logger.error(ex.getMessage());
        }
        finally {
            return eventAttendee;
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Object> remove(@PathVariable("uid") String uid) throws EventAttendeeNotFoundException {

        Map<String, List<String>> response = new HashMap<>(1);

        try {
            if (!uid.isEmpty()) {
                List<EventAttendee> eventAttendeeList = service.findByUid(uid);
                if (eventAttendeeList.size() >0 && eventAttendeeList.get(0) == null) {
                    throw new EventAttendeeNotFoundException(uid);
                }

                service.delete(eventAttendeeList.get(0));


                List<String> details = new ArrayList<>();
                details.add("EventAttend with UID: " + uid + " removed");

                response.put(TABLE_NAME, details);

                return new ResponseEntity<>(response, HttpStatus.OK);

            }
        } catch (EventAttendeeNotFoundException pe) {
            return new ResponseEntity<>(NOT_FOUND, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/checkin")
    public ResponseEntity<Object> checkin(
            @RequestParam(value = "eventUID") String eventUid,
            @RequestParam(value = "personUID") String personUid,
            @RequestParam(value = "checkIn") Boolean checkin) throws EventAttendeeNotFoundException
    {

        SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd HH:mm:ss");
        Date d = new Date();
        EventAttendee eventAttendee = new EventAttendee();
        try {
            //Check for input values
            if ((eventUid.isEmpty() || eventUid.equals("")) &&
                    (personUid.isEmpty() || personUid.equals(""))) {
                throw new EventAttendeeNotFoundException("Event UID: " + eventUid + " personUid: " + personUid);
            }

            //Find the EventAttendee Record
            List<EventAttendee> eventAttendees = service.findByEventUidAndPersonUid(eventUid, personUid);
            if (eventAttendees.size() > 0) {
                //Update the record with the value for Checkin
                eventAttendee = eventAttendees.get(0);
                if (checkin) {
                    eventAttendee.setCheckedIn("true");
                } else {
                    eventAttendee.setCheckedIn("false");
                }

                eventAttendee.setLastUpdated(sdf.format(d));

                eventAttendee = service.update(eventAttendee);

                Map<String, EventAttendee> response = new HashMap<>(1);
                response.put(TABLE_NAME, eventAttendee);

                //Record the change in the EventAttendeeHist table
                EventAttendeeHist eventHistRecord = new EventAttendeeHist();
                eventHistRecord.setEventUid(eventAttendee.getEventUid());
                eventHistRecord.setPersonUid(eventAttendee.getPersonUid());
                eventHistRecord.setPhotoPermission(eventAttendee.getPhotoPermission());
                eventHistRecord.setCheckedIn(eventAttendee.getCheckedIn());
                eventHistRecord.setSortKey(eventAttendee.getSortKey());


                eventHistRecord.setHistDate(sdf.format(d));
                eventHistRecord.setUid(EventUtils.generateType1UUID().toString());
                histService.createRecord(eventHistRecord);



                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        catch (EventAttendeeException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> exceptionResponse = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            exceptionResponse.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    finally {
            Map<String, EventAttendee> response = new HashMap<>(1);
            response.put(TABLE_NAME, eventAttendee);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

}
