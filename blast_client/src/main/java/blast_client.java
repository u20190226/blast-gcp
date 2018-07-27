/*===========================================================================
*
*                            PUBLIC DOMAIN NOTICE
*               National Center for Biotechnology Information
*
*  This software/database is a "United States Government Work" under the
*  terms of the United States Copyright Act.  It was written as part of
*  the author's official duties as a United States Government employee and
*  thus cannot be copyrighted.  This software/database is freely available
*  to the public for use. The National Library of Medicine and the U.S.
*  Government have not placed any restriction on its use or reproduction.
*
*  Although all reasonable efforts have been taken to ensure the accuracy
*  and reliability of the software and data, the NLM and the U.S.
*  Government do not and cannot warrant the performance or results that
*  may be obtained by using this software or data. The NLM and the U.S.
*  Government disclaim all warranties, express or implied, including
*  warranties of performance, merchantability or fitness for any particular
*  purpose.
*
*  Please cite the author in any work or product based on this material.
*
* ===========================================================================
*
*/

package gov.nih.nlm.ncbi.blast_client;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringWriter;
import java.net.Socket;

public final class blast_client
{

	public static String call_server( int port, final String query )
	{
		String res = "";
        try
        {
		    Socket socket = new Socket( "localhost", port );
		    socket.setTcpNoDelay( true );
		 	PrintStream ps = new PrintStream( socket.getOutputStream() );
            BufferedReader br = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

			res = br.readLine();
			//System.out.println( String.format( "rec: %s", res ) );

			ps.printf( query );
			socket.shutdownOutput();

			res = br.readLine();
            socket.close();
		}
        catch( Exception e )
        {
            System.out.println( String.format( "process : %s", e ) );
        }
		return res;
	}

	public static int run_cmd( String cmd, int port )
	{
		int res = -1;
		try
		{
			Process p = new ProcessBuilder( cmd, String.format( "%d", port ) ).start();
			BufferedReader br = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
			String line;
			while ( ( line = br.readLine()) != null )
			{
	  			System.out.println( line );
			}
			p.waitFor();
			res = p.exitValue();
		}
        catch( Exception e )
        {
            System.out.println( String.format( "run_cmd : %s", e ) );
        }
		return res;
	}

	public static void process_db_list( int port, request_obj ro, final String db_locations )
	{
		try
		{
		    BufferedReader reader = new BufferedReader( new FileReader( db_locations ) );
			String db_location;
			while ( ( db_location = reader.readLine() ) != null )
			{
				if ( !db_location.isEmpty() && !db_location.startsWith( "#" ) )
				{
					String reply = call_server( port, ro.toJson( db_location ) );

					String result_filename = String.format( "%s-%s.res", ro.RID, json_utils.get_last_part( db_location ) );
					int l = reply.length();
					if ( l > 0 )
					{
						json_utils.writeStringToFile( result_filename, reply );
						tb_list tbl = new tb_list( reply );
						tbl.write_to_file( String.format( "%s.asn1", result_filename ) );
					}
					System.out.println( String.format( "'%s' written ( l = %d )", result_filename, l ) );
				}
			}
		    reader.close();
		}
        catch( Exception e )
        {
            System.out.println( String.format( "process_db_list : %s", e ) );
        }
	}

	public static void process_request_list( int port, final String request_list_path, final String db_locations )
	{
		try
		{
		    BufferedReader reader = new BufferedReader( new FileReader( request_list_path ) );
			String request_path;
			while ( ( request_path = reader.readLine() ) != null )
			{
				if ( !request_path.isEmpty() && !request_path.startsWith( "#" ) )
				{
					String org_query = json_utils.readFileAsString( request_path );
					if ( !org_query.isEmpty() )
						process_db_list( port, new request_obj( org_query ), db_locations );
					else
						System.out.println( String.format( "request: '%s' not found or empty", request_path ) );
				}
			}
		    reader.close();
		}
        catch( Exception e )
        {
            System.out.println( String.format( "process_request_list : %s", e ) );
        }

	}

    public static void main( String[] args )
    {
        if ( args.length < 3 )
            System.out.println( "port-number, request-list, and db_location are missing" );
        else
        {
            String s_port = args[ 0 ];
			int port = Integer.parseInt( s_port.trim() );
			String s_request_list_path = args[ 1 ];
			String s_db_locations = args[ 2 ];

			int res = run_cmd( "./blast_server", port );
			System.out.println( String.format( "return-code: %d", res ) );
			if ( res == 0 )
				process_request_list( port, s_request_list_path, s_db_locations );
        }
   }

}
