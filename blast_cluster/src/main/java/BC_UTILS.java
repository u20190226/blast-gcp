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

package gov.nih.nlm.ncbi.blast_spark_cluster;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.time.ZonedDateTime;

import java.util.List;

public class BC_UTILS
{
	public static boolean file_exists( final String filename )
	{
		File f = new File( filename );
		return f.exists();
	}

	public static boolean save_to_file( final List< String > lines, final String filename )
	{
		boolean res = false;
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter( new FileWriter( new File( filename ) ) );
			if ( writer != null )
			{
				for ( String line : lines )
						writer.write( String.format( "%s\n", line ) );
				res = true;
			}
		}
		catch( Exception e ) { e.printStackTrace(); }
		finally { try{ writer.close(); } catch( Exception e ) { e.printStackTrace(); } }
		return res;
	}

	public static String datetime()
	{
		return String.format( "%s", ZonedDateTime.now() );
	}
}
