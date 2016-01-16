package bgu.ac.il.submissionsystem.model;

import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        List<Element> test = document.getElementsMatchingText("Publisher");
        List<Element> els = document.getElementsByTag("tr");


        if (els.size() > 1) {
            els.remove(0);//lose the assignments header
            int[] i =new int[1];
            i[0]=0;
            while (i[0] < els.size()) {
                Assignment ass = parseAssignment(els, i);
                assignments.add(ass);
            }

        }
        else{
            throw new ParseError(new Exception("cannot parse assignments"));
        }
        ListHolder<Assignment> assignmentListHolder = new ListHolder<>(assignments);
        String courseIds = getParam("course-id");
        if (courseIds != null && !courseIds.isEmpty()) {
            assignmentListHolder.getProps().setProperty("courseId", courseIds);
        }


        return assignmentListHolder;

    }


    private Assignment parseAssignment(List<Element> els, int[] startIndex) {
            Assignment ass = new Assignment();
            boolean parsed = false;
            int index = startIndex[0];
            Element el = null;
            while (!parsed && startIndex[0] - index < 9) {
                el = els.get(index);
                Elements ths = el.getElementsByTag("th");
                Elements tds = el.getElementsByTag("td");
                if (!ths.isEmpty()) {
                    if (tds.isEmpty()) {
                        String s=ths.first().text();
                        String[] split=s.split("\\.");
                        if(split.length>1){
                            ass.setName(split[1]);
                            int order=Integer.parseInt(split[0]);
                            ass.setOrder(order);
                        }
                        else{
                            ass.setName(s);
                        }

                    } else {
                        String header = ths.first().text();
                        String tdText = tds.first().text();
                        switch (header) {
                            case "Publisher":
                                ass.setPublisher(tdText);
                                break;
                            case "Publish date":
                                ass.setPublishDate(Constants.parseDate(tdText));
                                break;
                            case "Deadline":
                                ass.setDeadline(Constants.parseDate(tdText));
                                break;
                            case "Obligatory":
                                ass.setObligatory(tdText.equalsIgnoreCase("yes"));
                                break;
                            case "Percentage":
                                int pIn=tdText.indexOf("%");
                                if(pIn!=-1){
                                    tdText=tdText.substring(0, pIn);

                                }
                                tdText=tdText.trim();
                                ass.setPrecentage(Double.parseDouble(tdText));
                                break;
                            case "Grade":
                                ass.setGrade(Double.parseDouble(tdText));
                                break;
                            default:
                        }
                    }

                }
                else {

                    String s = tds.first().text();
                    if (s.isEmpty()) {
                        parsed = true;

                    } else {


                        List<Element> linksHolder = tds.first().getAllElements();
                        if (!linksHolder.isEmpty()) {
                            linksHolder = linksHolder.get(0).getAllElements();
                            int assId = -1;
                            int groupId = -1;
                            for (Element lel : linksHolder) {
                                String l = lel.attr("href");
                                if (assId == -1) {
                                    int l1 = l.indexOf("assignment-id=");
                                    if (l1 != -1) {
                                        l1 = l1 + "assignment-id=".length();
                                        String subl = l.substring(l1, l.length());
                                        int l2 = subl.indexOf("&");
                                        String assIds=subl;
                                        if(l2!=-1){
                                            assIds = subl.substring(0, l2);
                                        }

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
                                            String groupIds = subl.substring(0, l4);
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
                        }


                    }
                }
                index++;

            }
            startIndex[0]=index;
            return ass;

    }
}