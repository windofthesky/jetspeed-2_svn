/**
 * SWFHeader is (c) 2006 Paul Brooks Andrus and is released under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * http://www.brooksandrus.com/blog/2006/08/01/lightweight-swf-header-reader-java/
 * com.brooksandrus.utils.swf
 * 
 * Modified to efficiently read only the swf header (Steve Milek)
 * 
 */
package org.apache.portals.gems.flash;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author brooks
 * 
 */
public class SWFHeader
{
    protected Log log = LogFactory.getLog( SWFHeader.class );
    public static final String COMPRESSED   = "compressed";
    public static final String UNCOMPRESSED = "uncompressed";
    
    private String       signature;
    private String       compressionType;
    private int          version;
    private long         size;
    private int          nbits;
    private int          xmax;
    private int          ymax;
    private int          width;
    private int          height;
    private int          frameRate;
    private int          frameCount;

    public SWFHeader()
    {
        super();
    }   
   
    public boolean parseHeader( String fileName )
    {
        if ( fileName == null )
        {
            log.error( "Name for SWF file is null" );
            return false;
        }
        return manageInputStreamAndParseHeader( null, new File( fileName ) );
    }
   
    public boolean parseHeader( File file )
    {
        return manageInputStreamAndParseHeader( null, file );
    }
   
    public boolean parseHeader( InputStream is )
    {
        return manageInputStreamAndParseHeader( is, null );
    }
   
    private boolean manageInputStreamAndParseHeader( InputStream is, File file )
    {
        boolean inputIsSWF = false;
        try
        {
            if ( is == null && file != null )
            {
                is = new FileInputStream( file );
            }
            inputIsSWF = doParseHeader( is );
        }
        catch ( FileNotFoundException fnfEx )
        {
            log.error( "SWF file could not be found", fnfEx );
            inputIsSWF = false;
        }
        catch ( Exception e )
        {
            log.error( "Failed to parse SWF input", e );
            inputIsSWF = false;
        }
        finally
        {
            try
            {
                if ( is != null )
                {
                    is.close();
                }
            }
            catch ( Exception ex )
            {
                log.error( "Failed to close SWF InputStream", ex );
                inputIsSWF = false;
            }
        }
        return inputIsSWF;
    }
  
    private boolean doParseHeader( InputStream is ) throws Exception
    {
        byte[] temp = new byte[128];   // header is 47 bytes - we must read more in case compression is used (see uncompressHeader comments)
        
        byte[] swf = null;

        is.read( temp );

        if ( !isSWF( temp ) )
        {
            log.error( "Input does not match SWF format - incorrect file signature" );
            return false;
        }
        else
        {
            signature = "" + ( char ) temp[0] + ( char ) temp[1]
                + ( char ) temp[2];
        }

        if ( isCompressed( temp[0] ) )
        {
            swf = uncompressHeader( temp );
            compressionType = SWFHeader.COMPRESSED;
        }
        else
        {
            swf = temp;
            compressionType = SWFHeader.UNCOMPRESSED;
        }

        //System.out.println( "swf byte array length: " + swf.length );
       
        // version is the 4th byte of a swf; 
        version = swf[3];

        // bytes 5 - 8 represent the size in bytes of a swf
        size = readSize( swf );

        // Stage dimensions are stored in a rect

        nbits = ( ( swf[8] & 0xff ) >> 3 );

        PackedBitObj pbo = readPackedBits( swf, 8, 5, nbits );
       
        PackedBitObj pbo2 = readPackedBits( swf, pbo.nextByteIndex,
                                            pbo.nextBitIndex, nbits );

        PackedBitObj pbo3 = readPackedBits( swf, pbo2.nextByteIndex,
                                            pbo2.nextBitIndex, nbits );

        PackedBitObj pbo4 = readPackedBits( swf, pbo3.nextByteIndex,
                                            pbo3.nextBitIndex, nbits );

        xmax = pbo2.value;
        ymax = pbo4.value;

        width = convertTwipsToPixels( xmax );
        height = convertTwipsToPixels( ymax );

        int bytePointer = pbo4.nextByteIndex + 2;

        frameRate = swf[bytePointer];
        bytePointer++;
       
       
        int fc1 = swf[bytePointer] & 0xFF;
        bytePointer++;
       
        int fc2 = swf[bytePointer] & 0xFF;
        bytePointer++;
       
        frameCount = ( fc2 << 8 ) + fc1;
       
        dumpHeaderToStdOut();

        return true;
    }

    public void read( byte[] output, byte[] input, int offset )
    {
        System.arraycopy( input, offset, output, 0, output.length - offset );
    }

    public PackedBitObj readPackedBits( byte[] bytes, int byteMarker,
                                        int bitMarker, int length )
    {
        int total = 0;
        int shift = 7 - bitMarker;
        int counter = 0;
        int bitIndex = bitMarker;
        int byteIndex = byteMarker;
      
        while ( counter < length )
        {
            for ( int i = bitMarker; i < 8; i++ )
            {
                int bit = ( ( bytes[byteMarker] & 0xff ) >> shift ) & 1;
                total = ( total << 1 ) + bit;
                bitIndex = i;
                shift--;
                counter++;
            
                if ( counter == length )
                {
                    break;
                }
            }
            byteIndex = byteMarker;
            byteMarker++;
            bitMarker = 0;
            shift = 7;
        }
        return new PackedBitObj( bitIndex, byteIndex, total );
    }

    public int convertTwipsToPixels( int twips )
    {
        return twips / 20;
    }

    public int convertPixelsToTwips( int pixels )
    {
        return pixels * 20;
    }

    public boolean isSWF( byte[] signature )
    {
        String sig = "" + ( char ) signature[0] + ( char ) signature[1]
            + ( char ) signature[2];

        if ( sig.equals( "FWS" ) || sig.equals( "CWS" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isCompressed( int firstByte )
    {
        if ( firstByte == 67 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
   
    public boolean isCompressed()
    {
        boolean result = false;
        if ( signature.equalsIgnoreCase( "CWS" ) )
        {
            result = true;
        }
        return result;
    }

    // Inflator class will be upset if we let it read to the end of input and it discovers that
    // the zip format is not properly terminated - therefore, we must provide Inflater with more input
    // than what we intend to inflate
    protected byte[] uncompressHeader( byte[] bytes ) throws DataFormatException
    {  
        Inflater decompressor = new Inflater();
        byte[] compressed = strip( bytes );
        decompressor.setInput( compressed );   // feed the Inflater the bytes
        byte[] buffer = new byte[ 56 ];
        int count = decompressor.inflate( buffer );   // decompress the data into the buffer
        decompressor.end();
        
        //create an array to hold the header and body bytes
        byte[] swf = new byte[ 8 + count ];
        //copy the first 8 bytes which are uncompressed into the swf array
        System.arraycopy( bytes, 0, swf, 0, 8 );
        //copy the uncompressed data into the swf array
        System.arraycopy( buffer, 0, swf, 8, count );
        //the first byte of the swf indicates whether the swf is compressed or not
        swf[0] = 70;
      
        return swf;
    }

    // This version of uncompressHeader may be safer (because it uses InflaterInputStream),
    // but until there is some evidence of this we will stick with the direct approach of
    // using Inflater (above).
    /*
      protected byte[] uncompressHeader( byte[] headerBytes ) throws IOException
      {
      byte[] compressed = strip( headerBytes );
      InflaterInputStream iis = new InflaterInputStream( new ByteArrayInputStream( compressed ) );
      byte[] buffer = new byte[56];
      int bytesRead = iis.read( buffer, 0, buffer.length );
      iis.close();

      byte[] swfHeader = new byte[ 8 + bytesRead ];
        
      // copy the first 8 bytes which are uncompressed into the swf array
      System.arraycopy( headerBytes, 0, swfHeader, 0, 8 );
        
      // copy the uncompressed data into the swf array
      System.arraycopy( buffer, 0, swfHeader, 8, bytesRead );
        
      // the first byte of the swf indicates whether the swf is compressed or not
      swfHeader[0] = 70;
        
      return swfHeader;
      }
    */

    public int readSize( byte[] bytes )
    {
        int s = 0;
        for ( int i = 0; i < 4; i++ )
        {
            s = ( s << 8 ) + bytes[i + 4];
        }

        s = Integer.reverseBytes( s ) - 1;

        return s;
    }
    
    public byte[] strip( byte[] bytes )
    {
        byte[] compressable = new byte[bytes.length - 8];
        System.arraycopy( bytes, 8, compressable, 0, bytes.length - 8 );//fills a byte array with data needing decompression
        return compressable;
    }

    
    /**
     * @param args
     */
    public static void main( String[] args )
    {
        if ( args.length != 1 )
        {
            System.err.println( "usage: swf_file" );
        }
        else
        {
            try
            {
                SWFHeader swfH = new SWFHeader();
                if ( swfH.parseHeader( args[0] ) )
                {
                    swfH.dumpHeaderToStdOut();
                }
            }
            catch ( Exception e )
            {
                System.err.println( e.getMessage() );
            }
        }
        
    }

    public void dumpHeaderToStdOut()
    {
        System.out.println( "signature:   " + getSignature() );
        System.out.println( "version:     " + getVersion() );
        System.out.println( "compression: " + getCompressionType() );
        System.out.println( "size:        " + getSize() );
        System.out.println( "nbits:       " + getNbits() );
        System.out.println( "xmax:        " + getXmax() );
        System.out.println( "ymax:        " + getYmax() );
        System.out.println( "width:       " + getWidth() );
        System.out.println( "height:      " + getHeight() );
        System.out.println( "frameRate:   " + getFrameRate() );
        System.out.println( "frameCount:  " + getFrameCount() );
    }

    /**
     * @return the frameCount
     */
    public int getFrameCount()
    {
        return frameCount;
    }

    /**
     * @return the frameRate
     */
    public int getFrameRate()
    {
        return frameRate;
    }

    /**
     * @return the nbits
     */
    public int getNbits()
    {
        return nbits;
    }

    /**
     * @return the signature
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    /**
     * @return the version
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * @return the xmax
     */
    public int getXmax()
    {
        return xmax;
    }

    /**
     * @return the ymax
     */
    public int getYmax()
    {
        return ymax;
    }

    /**
     * @return the compressionType
     */
    public String getCompressionType()
    {
        return compressionType;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

}
