//start of CRC16.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CRC16.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
 * 
 * �ȉ��̏����ɓ��ӂ���Ȃ�΃\�[�X�ƃo�C�i���`���̍Ĕz�z�Ǝg�p��
 * �ύX�̗L���ɂ�����炸������B
 * 
 * �P�D�\�[�X�R�[�h�̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐�������ێ����Ȃ��Ă͂Ȃ�Ȃ��B
 * 
 * �Q�D�o�C�i���`���̍Ĕz�z�ɂ����Ē��쌠�\���� ���̏����̃��X�g
 *     ����щ��L�̐��������g�p�������������� ���̑��̔z�z������
 *     �܂ގ����ɋL�q���Ȃ���΂Ȃ�Ȃ��B
 * 
 * ���̃\�t�g�E�F�A�͐Β˔���ڂɂ���Ė��ۏ؂Œ񋟂���A����̖�
 * �I��B���ł���Ƃ����ۏ؁A���i���l���L��Ƃ����ۏ؂ɂƂǂ܂炸�A
 * �����Ȃ閾���I����шÎ��I�ȕۏ؂����Ȃ��B
 * �Β˔���ڂ� ���̃\�t�g�E�F�A�̎g�p�ɂ�钼�ړI�A�ԐړI�A����
 * �I�A����ȁA�T�^�I�ȁA���邢�͕K�R�I�ȑ��Q(�g�p�ɂ��f�[�^��
 * �����A�Ɩ��̒��f�〈���܂�Ă������v�̈⎸�A��֐��i��������
 * �T�[�r�X�̓�������l�����邪�A�����Ă��ꂾ���Ɍ��肳��Ȃ�
 * ���Q)�ɑ΂��āA�����Ȃ鎖�Ԃ̌����ƂȂ����Ƃ��Ă��A�_���̐�
 * �C�△�ߎ��ӔC���܂� �����Ȃ�ӔC�����낤�Ƃ��A���Ƃ����ꂪ�s
 * ���s�ׂ̂��߂ł������Ƃ��Ă��A�܂��͂��̂悤�ȑ��Q�̉\������
 * ������Ă����Ƃ��Ă���؂̐ӔC�𕉂�Ȃ����̂Ƃ���B
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces
import java.util.zip.Checksum;

//import exceptions

/**
 * CRC16�l���Z�o���邽�߂̃N���X�B
 * 
 * �N���X���̒萔�A�����A������
 * <pre>
 * �b����ɂ��A���S���Y�����T
 *   �������F�� �Z�p�]�_�� 
 *   ISBN4-87408-414-1 C3055 2400�~(�w������)
 * </pre>
 * �ɂ�����B
 * 
 * <pre>
 * -- revision history --
 * $Log: CRC16.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintanance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̕ύX
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class CRC16 implements Checksum{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int CRC_ANSY_POLY
    //  public static final int CRC_ANSY_INIT
    //  public static final int CCITT_POLY
    //  public static final int CCITT_INIT
    //  public static final int DefaultPOLY
    //  public static final int DefaultINIT
    //------------------------------------------------------------------
    /**
     * CRC-ANSY �܂��� CRC-16 �Ƃ��ėL����
     * ������ x^16 + x^15 + x^2 + 1 ���r�b�g�\���ɂ������́B
     */
    public static final int CRC_ANSY_POLY = 0xA001;

    /**
     * LHA�Ŏg�p����� crc �̏����l�B
     * ��҂�����ɐݒ肵���l�ł���A
     * CRC-ANSY �ł��̒l�������l�Ƃ���
     * ��߂��Ă��邩�͒m��Ȃ��B
     */
    public static final int CRC_ANSY_INIT = 0x0000;

    /**
     * CCITT �� X.25�Ƃ����K�i��
     * ������ x^16 + x^12 + x^5 + 1 ���r�b�g�\���ɂ������́B
     */
    public static final int CCITT_POLY = 0x8408;

    /**
     * CCITT �� X.25�Ƃ����K�i�� crc �̏����l�B
     */
    public static final int CCITT_INIT = 0xFFFF;

    /**
     * LHA�Œʏ�g�p�����A�Ƃ����Ӗ��Ńf�t�H���g��CRC�������B
     * CRC16.CRC_ANSY_POLY �Ɠ����ł���B
     */
    public static final int DefaultPOLY = CRC16.CRC_ANSY_POLY;

    /**
     * LHA�Œʏ�g�p�����A�Ƃ����Ӗ��Ńf�t�H���g��crc�̏����l�B
     * CRC16.CRC_ANSY_INIT �Ɠ����ł���B
     */
    public static final int DefaultINIT = CRC16.CRC_ANSY_INIT;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int crc
    //  private int init
    //  private int[] crcTable
    //------------------------------------------------------------------
    /** 
     * CRC16�l 
     */
    private int crc;

    /** 
     * crc �̏����l 
     */
    private int init;

    /** 
     * CRC16�l�̍X�V�p�e�[�u�� 
     */
    private int[] crcTable;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public CRC16()
    //  public CRC16( int poly )
    //  public CRC16( int poly, int init )
    //  public CRC16( int[] crcTable, int init )
    //------------------------------------------------------------------
    /**
     * LHA�Ŏg�p����� �������Ə����l������ CRC16�𐶐�����B
     */
    public CRC16(){
        this( DefaultPOLY, DefaultINIT );
    }

    /**
     * poly �Ŏw�肳��� ������������ CRC16�𐶐�����B
     * �����l�� poly �� CRC16.CCITT_POLY �ł����
     * CRC16.CCITT_INIT �� �����łȂ���� 
     * CRC16.DefaultINIT ���g�p����B
     * 
     * @param poly CRC16�Z�o�Ɏg�p���鑽�����̃r�b�g�\��
     */
    public CRC16( int poly ){
        this( poly, 
              ( poly == CRC16.CCITT_POLY ? 
                        CRC16.CCITT_INIT : 
                        CRC16.DefaultINIT ) );
    }

    /**
     * poly �Ŏw�肳��� �������� init�Ŏw�肳��鏉���l������
     * CRC16�𐶐�����B
     * 
     * @param poly CRC16�Z�o�Ɏg�p���鑽�����̃r�b�g�\��
     * @param init crc �̏����l
     */
    public  CRC16( int poly, int init ){
        this( CRC16.makeCrcTable( poly ), init );
    }

    /**
     * crcTable �Ŏw�肳��� CRC�Z�o�p�\�� 
     * init�Ŏw�肳��鏉���l������ CRC16���쐬����B
     *
     * @param crcTable CRC16�Z�o�Ɏg�p����\
     * @param init     crc �̏����l
     */
    public  CRC16( int[] crcTable, int init ){
        final int BYTE_PATTERNS= 256;

        if( crcTable.length == BYTE_PATTERNS ){
            this.crcTable = crcTable;
            this.init     = init;

            this.reset();
        }else{
            throw new IllegalArgumentException( "crcTable.length must equals 256" );
        }
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * byte8 �Ŏw�肳��� 1�o�C�g�̃f�[�^�� crc�̒l���X�V����B
     * 
     * @param byte8 crc���X�V���� 1�o�C�g�̃f�[�^
     */
    public void update( int byte8 ){
        final int BYTE_BITS = 8;
        this.crc = ( this.crc >> BYTE_BITS )
                    ^ this.crcTable[ ( this.crc ^ byte8 ) & 0xFF ];
    }

    /**
     * buffer �Ŏw�肵���o�C�g�z��� crc �̒l���X�V����B
     * 
     * @param buffer crc���X�V���� �f�[�^�����o�C�g�z��
     */
    public void update( byte[] buffer ){
        this.update( buffer, 0, buffer.length );
    }

    /**
     * buffer �Ŏw�肵���o�C�g�z��� crc �̒l���X�V����B
     * 
     * @param buffer crc���X�V���� �f�[�^�����o�C�g�z��
     * @param index  �f�[�^�̊J�n�ʒu
     * @param length �`�F�b�N�T���̍X�V�Ɏg���o�C�g��
     */
    public void update( byte[] buffer, int index, int length ){
        final int BYTE_BITS = 8;

        while( 0 < ( index & 0x03 ) && 0 < length-- ){
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ buffer[index++] ) & 0xFF ];
        }

        while( 4 <= length ){
            int data =  (   buffer[index++] & 0xFF )
                      | ( ( buffer[index++] & 0xFF ) <<  8 )
                      | ( ( buffer[index++] & 0xFF ) << 16 )
                      | (   buffer[index++]          << 24 );

            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            length -= 4;
        }

        while( 0 < length-- ){
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ buffer[index++] ) & 0xFF ];
        }
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void reset()
    //  public long getValue()
    //------------------------------------------------------------------
    /**
     * crc �l�������l�ɐݒ肵�Ȃ����B
     */
    public void reset(){
        this.crc = this.init;
    }

    /**
     * crc �l�𓾂�B
     * crc �l�� 2�o�C�g�̒l�ł���A 
     * 0x0000�`0xFFFF�Ƀ}�b�v�����B
     * 
     * @return crc �l
     */
    public long getValue(){
        return this.crc & 0xFFFF;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  public static int[] makeCrcTable( int init )
    //------------------------------------------------------------------
    /**
     * CRC�l�Z�o�p�� �\���쐬����B
     * 
     * @param poly CRC�Z�o�p�̑�����
     */
    public static int[] makeCrcTable( int poly ){
        final int BYTE_PATTERNS = 256;
        final int BYTE_BITS     = 8;
        int[] crcTable = new int[BYTE_PATTERNS];

        for( int i = 0 ; i < BYTE_PATTERNS ; i++ ){
            crcTable[i] = i;

            for( int j = 0 ; j < BYTE_BITS ; j++ ){
                if( ( crcTable[i] & 1 ) != 0 ){
                    crcTable[i] = ( crcTable[i] >> 1 ) ^ poly;
                }else{
                    crcTable[i] >>= 1;
                }
            }
        }

        return crcTable;
    }

}
//end of CRC16.java
