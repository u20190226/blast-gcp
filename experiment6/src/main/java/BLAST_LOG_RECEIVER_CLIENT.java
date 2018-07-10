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

package gov.nih.nlm.ncbi.blastjni;

import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class BLAST_LOG_RECEIVER_CLIENT extends Thread
{
    private final BLAST_STATUS status;
    private final BLAST_LOG_WRITER writer;
    private final Socket socket;

    public BLAST_LOG_RECEIVER_CLIENT( BLAST_STATUS a_status, Socket a_socket, final BLAST_LOG_WRITER a_writer )
    {
        this.status = a_status;
        this.socket = a_socket;
		this.writer = a_writer;
    }
    
    @Override public void run()
    {
        try
        {
            socket.setTcpNoDelay( true );
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            String input;

            while( status.is_running() && ( ( input = in.readLine() ) != null ) ) 
            {
				writer.write( input.trim() );
            }
            socket.close();
        }
        catch ( Exception e )
        {
            System.out.println( String.format( "BLAST_LOG_RECEIVER_CLIENT: %s", e ) );
        }
    }
}

