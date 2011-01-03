package org.apache.maven.plugin.trac;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.issues.Issue;
import org.apache.maven.plugin.issues.IssuesReportGenerator;
import org.apache.maven.reporting.MavenReportException;

/**
 * Generates a Trac report.
 *
 * @author Noriko Kinugasa
 * @version $Id$
 */
public class TracReportGenerator
{
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TYPE = 1;
    private static final int COLUMN_SUMMARY = 2;
    private static final int COLUMN_STATUS = 3;
    private static final int COLUMN_RESOLUTION = 4;
    private static final int COLUMN_MILESTONE = 5;
    private static final int COLUMN_OWNER = 6;
    private static final int COLUMN_PRIORITY = 7;
    private static final int COLUMN_REPORTER = 8;
    private static final int COLUMN_COMPONENT = 9;
    private static final int COLUMN_CREATED = 10;
    private static final int COLUMN_CHANGED = 11;

    private int[] columnOrder;

    public TracReportGenerator( String columnNames, String[] columns )
        throws MavenReportException
    {
        String[] columnNamesArray = columnNames.split( "," );
        int validColumnNames = 0;
        columnOrder = new int[columnNamesArray.length];
        for ( int i = 0; i < columnOrder.length; i++ )
        {
            // Default to -1, indicating that the column should not be included in the report
            columnOrder[i] = -1;
            for ( int columnIndex = 0; columnIndex < columns.length; columnIndex++ )
            {
                String columnName = columnNamesArray[i].trim();
                if ( columns[columnIndex].equalsIgnoreCase( columnName ) )
                {
                    // Found a valid column name - add it
                    columnOrder[i] = columnIndex;
                    validColumnNames++;
                    break;
                }
            }
        }
        if ( validColumnNames == 0 )
        {
            // This can happen if the user has configured column names and they are all invalid
            throw new MavenReportException(
                "maven-changes-plugin: None of the configured columnNames '" + columnNames + "' are valid." );
        }
    }

    public void doGenerateEmptyReport( ResourceBundle bundle, Sink sink )
    {
        sinkBeginReport( sink, bundle );

        sink.paragraph();

        sink.text( bundle.getString( "report.trac.error" ) );

        sink.paragraph_();

        sinkEndReport( sink );
    }

    public void doGenerateReport( ResourceBundle bundle, Sink sink, List issueList )
    {
        sinkBeginReport( sink, bundle );

        constructHeaderRow( sink, issueList, bundle );

        constructDetailRows( sink, issueList, bundle );

        sinkEndReport( sink );
    }

    private void constructHeaderRow( Sink sink, List issueList, ResourceBundle bundle )
    {
        if ( issueList == null )
        {
            return;
        }

        sink.table();

        sink.tableRow();

        for ( int columnIndex = 0; columnIndex < columnOrder.length; columnIndex++ )
        {
            switch ( columnOrder[columnIndex] )
            {
                case COLUMN_ID:
                    sinkHeader( sink, bundle.getString( "report.trac.label.id" ) );
                    break;
                case COLUMN_TYPE:
                    sinkHeader( sink, bundle.getString( "report.trac.label.type" ) );
                    break;
                case COLUMN_SUMMARY:
                    sinkHeader( sink, bundle.getString( "report.trac.label.summary" ) );
                    break;
                case COLUMN_OWNER:
                    sinkHeader( sink, bundle.getString( "report.trac.label.owner" ) );
                    break;
                case COLUMN_REPORTER:
                    sinkHeader( sink, bundle.getString( "report.trac.label.reporter" ) );
                    break;
                case COLUMN_PRIORITY:
                    sinkHeader( sink, bundle.getString( "report.trac.label.priority" ) );
                    break;
                case COLUMN_STATUS:
                    sinkHeader( sink, bundle.getString( "report.trac.label.status" ) );
                    break;
                case COLUMN_RESOLUTION:
                    sinkHeader( sink, bundle.getString( "report.trac.label.resolution" ) );
                    break;
                case COLUMN_CREATED:
                    sinkHeader( sink, bundle.getString( "report.trac.label.created" ) );
                    break;
                case COLUMN_CHANGED:
                    sinkHeader( sink, bundle.getString( "report.trac.label.changed" ) );
                    break;
                case COLUMN_MILESTONE:
                    sinkHeader( sink, bundle.getString( "report.trac.label.milestone" ) );
                    break;
                case COLUMN_COMPONENT:
                    sinkHeader( sink, bundle.getString( "report.trac.label.component" ) );
                    break;
                default:
                    // Do not add a header for this column
                    break;
            }
        }

        sink.tableRow_();
    }

    private void constructDetailRows( Sink sink, List issueList, ResourceBundle bundle )
    {
        if ( issueList == null )
        {
            return;
        }

        for ( int idx = 0; idx < issueList.size(); idx++ )
        {
            SimpleDateFormat sdf = new SimpleDateFormat( bundle.getString( "report.trac.dateformat" ) );

            Issue issue = (Issue) issueList.get( idx );

            sink.tableRow();

            for ( int columnIndex = 0; columnIndex < columnOrder.length; columnIndex++ )
            {
                switch ( columnOrder[columnIndex] )
                {
                    case COLUMN_ID:
                        sink.tableCell();
                        sink.link( issue.getLink() );
                        sink.text( issue.getId() );
                        sink.link_();
                        sink.tableCell_();
                        break;
                    case COLUMN_TYPE:
                        sinkCell( sink, issue.getType() );
                        break;
                    case COLUMN_SUMMARY:
                        sinkCell( sink, issue.getSummary() );
                        break;
                    case COLUMN_OWNER:
                        sinkCell( sink, issue.getAssignee() );
                        break;
                    case COLUMN_REPORTER:
                        sinkCell( sink, issue.getReporter() );
                        break;
                    case COLUMN_PRIORITY:
                        sinkCell( sink, issue.getPriority() );
                        break;
                    case COLUMN_STATUS:
                        sinkCell( sink, issue.getStatus() );
                        break;
                    case COLUMN_RESOLUTION:
                        sinkCell( sink, issue.getResolution() );
                        break;
                    case COLUMN_CREATED:
                        sinkCell( sink, sdf.format( issue.getCreated() ) );
                        break;
                    case COLUMN_CHANGED:
                        sinkCell( sink, sdf.format( issue.getUpdated() ) );
                        break;
                    case COLUMN_MILESTONE:
                        sinkCell( sink, IssuesReportGenerator.printValues( issue.getFixVersions() ) );
                        break;
                    case COLUMN_COMPONENT:
                        sinkCell( sink, IssuesReportGenerator.printValues( issue.getComponents() ) );
                        break;
                    default:
                        // Do not add details for this column
                        break;
                }
            }

            sink.tableRow_();
        }

        sink.table_();
    }

    private void sinkBeginReport( Sink sink, ResourceBundle bundle )
    {
        sink.head();

        sink.title();
        sink.text( bundle.getString( "report.trac.header" ) );
        sink.title_();

        sink.head_();

        sink.body();

        sink.section1();

        sinkSectionTitle1( sink, bundle.getString( "report.trac.header" ) );
    }

    private void sinkEndReport( Sink sink )
    {
        sink.section1_();

        sink.body_();

        sink.flush();

        sink.close();
    }

    private void sinkFigure( Sink sink, String image )
    {
        sink.figure();

        sink.figureGraphics( image );

        sink.figure_();
    }

    private void sinkHeader( Sink sink, String header )
    {
        sink.tableHeaderCell();

        sink.text( header );

        sink.tableHeaderCell_();
    }

    private void sinkCell( Sink sink, String text )
    {
        sink.tableCell();

        if ( text != null )
        {
            sink.text( text );
        }
        else
        {
            sink.rawText( "&nbsp;" );
        }

        sink.tableCell_();
    }

    private void sinkSectionTitle1( Sink sink, String text )
    {
        sink.sectionTitle1();

        sink.text( text );

        sink.sectionTitle1_();
    }
}
