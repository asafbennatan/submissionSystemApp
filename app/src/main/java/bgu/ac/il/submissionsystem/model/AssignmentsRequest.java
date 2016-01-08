package bgu.ac.il.submissionsystem.model;

import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bgu.ac.il.submissionsystem.Utils.Constants;

/**
 * Created by Asaf on 03/01/2016.
 */
public class AssignmentsRequest extends CustomSubmissionSystemRequest<ListHolder<Assignment>> {

    public AssignmentsRequest(String url, Response.Listener<ListHolder<Assignment>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, listener, errorListener);
    }

    @Override
    protected ListHolder<Assignment> createResponse(Document document) throws ParseError {
        List<Assignment> assignments = new ArrayList<>();
        List<Element> els = document.getElementsByAttributeValue("class", "header");
        if (els.size() > 1) {
            els.remove(0);//lose the assignments header
            for (Element el : els) {
                Assignment ass = parseAssignment(el);
                assignments.add(ass);
            }

        }


        return new ListHolder<>(assignments);

    }





    private Assignment parseAssignment(Element e) throws ParseError {
        List<Element> assignmentElements = e.getElementsByTag("td");
        if (assignmentElements.size() == 7) {
            Assignment ass = new Assignment();
            try {
                ass.setPublisher(assignmentElements.get(0).text());
                ass.setPublishDate(Constants.parseDate(assignmentElements.get(1).text()));
                ass.setDeadline(Constants.parseDate(assignmentElements.get(2).text()));
                ass.setGrade(Double.parseDouble(assignmentElements.get(3).text()));
                ass.setObligatory(assignmentElements.get(4).text().equalsIgnoreCase("yes"));
                ass.setPrecentage(Double.parseDouble(assignmentElements.get(5).text()));
                List<Element> linksHolder = assignmentElements.get(6).getAllElements();
                int assId = -1;
                int groupId = -1;
                for (Element lel : linksHolder) {
                    String l = lel.attr("herf");
                    if (assId == -1) {
                        int l1 = l.indexOf("assignment-id=");
                        if (l1 != -1) {
                            l1 = l1 + "assignment-id=".length();
                            String subl = l.substring(l1, l.length());
                            int l2 = subl.indexOf("&");
                            String assIds = subl.substring(0, l2 - 1);
                            assId = Integer.parseInt(assIds);
                        }

                        if (groupId == -1) {
                            int l3 = l.indexOf("submittal-group-id=");
                            if (l3 != -1) {
                                l3 = l3 + "submittal-group-id=".length();
                                String subl = l.substring(l3, l.length());
                                int l4 = subl.indexOf("&");
                                if (l4 == -1) {
                                    l4 = subl.length();
                                }
                                String groupIds = subl.substring(0, l4 - 1);
                                groupId = Integer.parseInt(groupIds);
                            }
                        }

                    }
                    if (assId != -1) {
                        ass.setId(assId);
                        if (groupId != -1) {
                            ass.setGroup(new Group(groupId));
                        }
                    }


                }
                return ass;
            } catch (Exception ex) {
                throw new ParseError(ex);
            }

        }
        throw new ParseError(new Exception("unable to parse Assignment"));

    }
}