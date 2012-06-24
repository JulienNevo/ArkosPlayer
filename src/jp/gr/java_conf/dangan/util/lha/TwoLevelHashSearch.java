//start of TwoLevelHashSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * TwoLevelHashSearch.java
 * 
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
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
 * ���̃\�t�g�E�F�A�͐Β˔��ڂɂ���Ė��ۏ؂Œ񋟂���A����̖�
 * �I��B���ł���Ƃ����ۏ؁A���i���l���L��Ƃ����ۏ؂ɂƂǂ܂炸�A
 * �����Ȃ閾���I����шÎ��I�ȕۏ؂����Ȃ��B
 * �Β˔��ڂ� ���̃\�t�g�E�F�A�̎g�p�ɂ�钼�ړI�A�ԐړI�A����
 * �I�A����ȁA�T�^�I�ȁA���邢�͕K�R�I�ȑ��Q(�g�p�ɂ��f�[�^��
 * �����A�Ɩ��̒��f�〈���܂�Ă������v�̈⎸�A��֐��i��������
 * �T�[�r�X�̓������l�����邪�A�����Ă��ꂾ���Ɍ��肳��Ȃ�
 * ���Q)�ɑ΂��āA�����Ȃ鎖�Ԃ̌����ƂȂ����Ƃ��Ă��A�_���̐�
 * �C�△�ߎ��ӔC���܂� �����Ȃ�ӔC�����낤�Ƃ��A���Ƃ����ꂪ�s
 * ���s�ׂ̂��߂ł������Ƃ��Ă��A�܂��͂��̂悤�ȑ��Q�̉\������
 * ������Ă����Ƃ��Ă���؂̐ӔC�𕉂�Ȃ����̂Ƃ���B
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.lang.reflect.Factory;
import jp.gr.java_conf.dangan.util.lha.HashShort;
import jp.gr.java_conf.dangan.util.lha.HashMethod;
import jp.gr.java_conf.dangan.util.lha.LzssOutputStream;
import jp.gr.java_conf.dangan.util.lha.LzssSearchMethod;

import java.lang.NoSuchMethodException;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;

import java.lang.Error;
import java.lang.NoSuchMethodError;
import java.lang.InstantiationError;
import java.lang.NoClassDefFoundError;


/**
 * ��i�K�n�b�V���ƒP���A�����X�g���g���č��������ꂽ LzssSearchMethod�B<br>
 * <a href="http://search.ieice.org/2000/pdf/e83-a_12_2689.pdf">�茓���̘_��</a>
 * ���Q�l�ɂ����B
 * 
 * <pre>
 * -- revision history --
 * $Log: TwoLevelHashSearch.java,v $
 * Revision 1.1  2002/12/10 22:06:40  dangan
 * [bug fix]
 *     searchAndPut() �ōŋ߂̍Œ���v�����Ȃ������o�O���C���B
 *
 * Revision 1.0  2002/12/03 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class TwoLevelHashSearch implements LzssSearchMethod{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //------------------------------------------------------------------
    /**
     * LZSS�����T�C�Y�B
     */
    private int DictionarySize;

    /**
     * LZSS���k�Ɏg�p�����l�B
     * �ő��v���������B
     */
    private int MaxMatch;

    /**
     * LZSS���k�Ɏg�p�����臒l�B
     * ��v���� ���̒l�ȏ�ł���΁A���k�R�[�h���o�͂���B
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS���k���{�����߂̃o�b�t�@�B
     * �O���͎����̈�A
     * �㔼�͈��k���{�����߂̃f�[�^�̓�����o�b�t�@�B
     * LzssSearchMethod�̎������ł͓ǂݍ��݂̂݋������B
     */
    private byte[] TextBuffer;

    /**
     * �����̌��E�ʒu�B 
     * TextBuffer�O���̎����̈�Ƀf�[�^�������ꍇ��
     * �����̈�ɂ���s��̃f�[�^(Java�ł�0)���g�p
     * ���Ĉ��k���s����̂�}�~����B
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  primary hash
    //------------------------------------------------------------------
    //  private HashMethod primaryHash
    //  private int[] primaryHashTable
    //  private int[] primaryCount
    //------------------------------------------------------------------
    /**
     * ��i�ڂ̃n�b�V���֐�
     */
    private HashMethod primaryHash;

    /**
     * ��i�ڂ̃n�b�V���e�[�u��
     * �Y���͈�i�ڂ̃n�b�V���l�A���e�� ��i�ڂ̃n�b�V���e�[�u���� index
     */
    private int[] primaryHashTable;

    /**
     * ��i�ڂ̃n�b�V���e�[�u���Ɋ�̃f�[�^�p�^����
     * �o�^����Ă��邩���J�E���g���Ă����B
     */
    private int[] primaryCount;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  secondary hash
    //------------------------------------------------------------------
    //  private int[] secondaryHashRequires
    //  private int[] secondaryHashTable
    //  private int[] dummy
    //------------------------------------------------------------------
    /**
     * ��i�ڂ̃n�b�V���l���Z�o���邽�߂ɕK�v�ȃo�C�g���B
     */
    private int[] secondaryHashRequires;

    /**
     * ��i�ڂ̃n�b�V���e�[�u��
     * �Y���� ��i�ڂ̃n�b�V���e�[�u���̒l + ��i�ڂ̃n�b�V���l�A
     * ���e�� TextBuffer ���̃f�[�^�p�^���̊J�n�ʒu
     */
    private int[] secondaryHashTable;

    /**
     * slide() �̖��� secondaryHashTable �Ɠ��ւ���_�~�[�z��B
     * �g���܂킵�p�B
     */
    private int[] dummy;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cahined list
    //------------------------------------------------------------------
    //  private int[] prev
    //------------------------------------------------------------------
    /**
     * �����n�b�V���l�����f�[�^�p�^���J�n�ʒu������
     * �P���A�����X�g�B
     */
    private int[] prev;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private TwoLevelHashSearch()
    //  public TwoLevelHashSearch( int DictionarySize, int MaxMatch, 
    //                             int Threshold, byte[] TextBuffer )
    //  public TwoLevelHashSearch( int DictionarySize, int MaxMatch, 
    //                             int Threshold, byte[] TextBuffer,
    //                             String HashMethodClassName )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private TwoLevelHashSearch(){ }

    /**
     * ��i�K�n�b�V�����g�p���� LzssSearchMethod ���\�z����B<br>
     * ��i�ڂ̃n�b�V���֐��ɂ� �f�t�H���g�̂��̂��g�p�����B<br>
     * 
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �ő��v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     */
    public TwoLevelHashSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer ){
        this( DictionarySize,
              MaxMatch,
              Threshold,
              TextBuffer,
              HashShort.class.getName() );
    }


    /**
     * ��i�K�n�b�V�����g�p���� LzssSearchMethod ���\�z����B
     * 
     * @param DictionarySize      �����T�C�Y
     * @param MaxMatch            �ő��v��
     * @param Threshold           ���k�A�񈳏k��臒l
     * @param TextBuffer          LZSS���k���{�����߂̃o�b�t�@
     * @param HashMethodClassName Hash�֐���񋟂���N���X��
     * 
     * @exception NoClassDefFoundError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��������Ȃ��ꍇ�B
     * @exception InstantiationError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              abstract class �ł��邽�߃C���X�^���X�𐶐��ł��Ȃ��ꍇ�B
     * @exception NoSuchMethodError
     *              HashMethodClassName �ŗ^����ꂽ�N���X��
     *              �R���X�g���N�^ HashMethod( byte[] )�������Ȃ��ꍇ�B
     */
    public TwoLevelHashSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer,
                               String HashMethodClassName ){

        this.DictionarySize   = DictionarySize;
        this.MaxMatch         = MaxMatch;
        this.Threshold        = Threshold;
        this.TextBuffer       = TextBuffer;
        this.DictionaryLimit  = this.DictionarySize;

        try{
            this.primaryHash = (HashMethod)Factory.createInstance(
                                                HashMethodClassName, 
                                                new Object[]{ TextBuffer } );
        }catch( ClassNotFoundException exception ){
            throw new NoClassDefFoundError( exception.getMessage() );
        }catch( InvocationTargetException exception ){
            throw new Error( exception.getTargetException().getMessage() );
        }catch( NoSuchMethodException exception ){
            throw new NoSuchMethodError( exception.getMessage() );
        }catch( InstantiationException exception ){
            throw new InstantiationError( exception.getMessage() );
        }

        // �n�b�V���e�[�u������
        this.primaryHashTable   = new int[ this.primaryHash.tableSize() ];
        this.secondaryHashTable = new int[ ( this.primaryHash.tableSize() 
                                           + this.DictionarySize / 4 ) ];
        for( int i = 0 ; i < this.primaryHashTable.length ; i++ ){
            this.primaryHashTable[i]   = i;
            this.secondaryHashTable[i] = -1;
        }

        // ���̑��̔z�񐶐� 
        // primaryCount �� secondaryHashRequires �͔z�񐶐����Ƀ[���N���A����Ă��鎖�𗘗p����B
        this.primaryCount          = new int[ this.primaryHash.tableSize() ];
        this.secondaryHashRequires = new int[ this.primaryHash.tableSize() ];
        this.dummy                 = new int[ this.secondaryHashTable.length ];

        // �A�����X�g����
        this.prev = new int[ this.DictionarySize ];
        for( int i = 0 ; i < this.prev.length ; i++ ){
            this.prev[i] = -1;
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide( int slideWidth, int slideEnd )
    //  public int putRequires()
    //------------------------------------------------------------------
    /**
     * position ����n�܂�f�[�^�p�^����
     * ��i�K�n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^����B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public void put( int position ){
        int phash = this.primaryHash.hash( position );
        int base  = this.primaryHashTable[ phash ];
        int shash = this.secondaryHash( position, this.secondaryHashRequires[ phash ] );

        this.primaryCount[ phash ]++;
        this.prev[ position & ( this.DictionarySize - 1 ) ] = 
                                        this.secondaryHashTable[ base + shash ];
        this.secondaryHashTable[ base + shash ] = position;
    }

    /**
     * ��i�K�n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^���ꂽ
     * �f�[�^�p�^������ position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂��������A
     * ������ position ����n�܂�f�[�^�p�^���� 
     * ��i�K�n�b�V���ƘA�����X�g���琬�錟���@�\�ɓo�^����B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu�B
     * 
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn 
     *         �ɂ���Đ������ꂽ��v�ʒu�ƈ�v���̏������l�A
     *         ��v��������Ȃ������ꍇ��
     *         LzssOutputStream.NOMATCH�B
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int searchAndPut( int position ){
        int matchlen  = this.Threshold - 1;
        int matchpos  = position;
        int scanlimit = Math.max( this.DictionaryLimit,
                                  position - this.DictionarySize );


        int phash    = this.primaryHash.hash( position );
        int base     = this.primaryHashTable[ phash ];
        int requires = this.secondaryHashRequires[ phash ];
        int shash    = this.secondaryHash( position, requires );
        int scanpos  = this.secondaryHashTable[ base + shash ];

        byte[] buf   = this.TextBuffer;
        int max      = position + this.MaxMatch;
        int s        = 0;
        int p        = 0;
        int len      = 0;

        //------------------------------------------------------------------
        //  ��i�ڂ̃n�b�V���ɂ���đI�΂ꂽ�A�����X�g���������郋�[�v
        while( scanlimit <= scanpos ){
            if( buf[ scanpos + matchlen ] == buf[ position + matchlen ] ){
                s = scanpos;
                p = position;
                while( buf[s] == buf[p] ){
                    s++;
                    p++;
                    if( max <= p ) break;
                }

                len = p - position;
                if( matchlen < len ){
                    matchpos = scanpos;
                    matchlen = len;
                    if( max <= p ) break;
                }
            }
            scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
        }

        //------------------------------------------------------------------
        //  ��i�ڂ̃n�b�V���ɂ���Č��I���ꂽ�A�����X�g�Ɉ�v�������ꍇ�A
        //  ��i�ڂ̃n�b�V���ɓo�^����Ă���S�Ă̘A�����X�g����������
        int revbits  = 1;
        int loopend  = requires - Math.max( 0, this.Threshold - this.primaryHash.hashRequires() );
        int maxmatch = this.primaryHash.hashRequires() + requires - 1;
        for( int i = 1, send = 4 ; i <= loopend && matchlen <= maxmatch ; i++, send <<= 2 ){
            max += position + maxmatch;
            while( revbits < send ){
                scanpos  = this.secondaryHashTable[ base + ( shash ^ revbits ) ];
                while( scanlimit <= scanpos ){
                    if( buf[ scanpos ] == buf[ position ] ){
                        s = scanpos + 1;
                        p = position + 1;
                        while( buf[s] == buf[p] ){
                            s++;
                            p++;
                            if( max <= p ) break;
                        }

                        len = p - position;
                        if( matchlen < len
                         || ( matchlen == len && matchpos < scanpos ) ){
                            matchpos = scanpos;
                            matchlen = len;
                            if( max <= p ){
                                scanlimit = scanpos;
                                break;
                            }
                        }
                    }
                    scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
                }
                revbits++;
            }
            maxmatch = this.primaryHash.hashRequires() + requires - i - 1;
        }
        
        //------------------------------------------------------------------
        //  ��i�K�n�b�V���ƘA�����X�g���g�p���������@�\��
        //  position ����n�܂�f�[�^�p�^����o�^����B
        this.primaryCount[ phash ]++;
        this.prev[ position & ( this.DictionarySize - 1 ) ] = 
                                        this.secondaryHashTable[ base + shash ];
        this.secondaryHashTable[ base + shash ] = position;

        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * �n�b�V���ƘA�����X�g���g�p���������@�\�ɓo�^���ꂽ
     * �f�[�^�p�^���������� position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂𓾂�B<br>
     * 
     * @param position   TextBuffer���̃f�[�^�p�^���̊J�n�ʒu�B
     * @param lastPutPos �Ō�ɓo�^�����f�[�^�p�^���̊J�n�ʒu�B
     * 
     * @return ��v�����������ꍇ��
     *         LzssOutputStream.createSearchReturn 
     *         �ɂ���Đ������ꂽ��v�ʒu�ƈ�v���̏������l�A
     *         ��v��������Ȃ������ꍇ��
     *         LzssOutputStream.NOMATCH�B
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int search( int position, int lastPutPos ){

        //------------------------------------------------------------------
        //  �n�b�V���ƘA�����X�g�ɂ�錟���@�\�ɓo�^����Ă��Ȃ�
        //  �f�[�^�p�^����P���Ȓ��������Ō�������B
        int matchlen   = this.Threshold - 1;
        int matchpos   = position;
        int scanpos    = position - 1;
        int scanlimit  = Math.max( this.DictionaryLimit, lastPutPos );

        byte[] buf     = this.TextBuffer;
        int max        = Math.min( this.TextBuffer.length,
                                   position + this.MaxMatch );
        int s          = 0;
        int p          = 0;
        int len        = 0;
        while( scanlimit < scanpos ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
            }
            scanpos--;
        }

        //------------------------------------------------------------------
        //  ��i�K�n�b�V���ƘA�����X�g���g�p���������@�\���猟������B
        int phashRequires = this.primaryHash.hashRequires();
        if( phashRequires < this.TextBuffer.length - position ){

            int phash    = this.primaryHash.hash( position );
            int base     = this.primaryHashTable[ phash ];
            int requires = this.secondaryHashRequires[ phash ];
            int shash;
            int start;
            if( phashRequires + requires < this.TextBuffer.length - position ){
                shash   = this.secondaryHash( position, requires );
                start   = 0;
            }else{
                int avail = this.TextBuffer.length - position - phashRequires;
                shash   = this.secondaryHash( position, avail ) << ( ( requires - avail ) * 2 );
                start   = requires - avail;
            }
            int revbits = 0;
            int loopend  = requires - Math.max( 0, this.Threshold - this.primaryHash.hashRequires() );
            int maxmatch = this.MaxMatch;

            //------------------------------------------------------------------
            //  ��i�ڂ̂ɓo�^����Ă���A�����X�g��D��x�̏��Ɍ������郋�[�v
            for( int i = start, send = ( 1 << ( i * 2 ) ) ; i <= requires ; i++, send <<= 2 ){
                max += position + maxmatch;
                while( revbits < send ){
                    scanpos  = this.secondaryHashTable[ base + ( shash ^ revbits ) ];
                    while( scanlimit <= scanpos ){
                        if( buf[ scanpos ] == buf[ position ] ){
                            s = scanpos + 1;
                            p = position + 1;
                            while( buf[s] == buf[p] ){
                                s++;
                                p++;
                                if( max <= p ) break;
                            }

                            len = p - position;
                            if( matchlen < len
                             || ( matchlen == len && matchpos < scanpos ) ){
                                matchpos = scanpos;
                                matchlen = len;
                                if( max <= p ){
                                    scanlimit = scanpos;
                                    break;
                                }
                            }
                        }
                        scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
                    }
                    revbits++;
                }
                maxmatch = this.primaryHash.hashRequires() + requires - i - 1;
            }
        }// if( phashRequires < this.TextBuffer.length - position )

        //------------------------------------------------------------------
        //  �Œ���v���Ăяo�����ɕԂ��B
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }

    }

    /**
     * TextBuffer����position�܂ł̃f�[�^��
     * �O��ֈړ�����ہA����ɉ����� SearchMethod����
     * �f�[�^�� TextBuffer���̃f�[�^�Ɩ������Ȃ��悤��
     * �O��ֈړ����鏈�����s���B
     */
    public void slide(){

        //------------------------------------------------------------------
        //  DictionaryLimit�X�V
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );

        //------------------------------------------------------------------
        //  primaryCount �̒l�ɂ���� secondaryHashTable ���č\������
        int secondaryIndex = 0;
        int dummyIndex     = 0;
        for( int i = 0 ; i < this.primaryHashTable.length ; i++ ){
            this.primaryHashTable[i] = dummyIndex;
            int bits = this.secondaryHashRequires[i] * 2;

            if( 1 << ( 5 + bits ) <= this.primaryCount[i] ){
                for( int j = 0 ; j < ( 1 << bits ) ; j++ ){
                    this.divide( dummyIndex, secondaryIndex, this.primaryHash.hashRequires() + this.secondaryHashRequires[i] );
                    dummyIndex     += 4;
                    secondaryIndex += 1;
                }
                this.secondaryHashRequires[i]++;

            }else if( 0 < bits && this.primaryCount[i] < ( 1 << ( 2 + bits ) ) ){
                for( int j = 0 ; j < ( 1 << ( bits - 2 ) ) ; j++ ){
                    this.merge( dummyIndex, secondaryIndex );
                    dummyIndex     += 1;
                    secondaryIndex += 4;
                }
                this.secondaryHashRequires[i]--;

            }else{
                for( int j = 0 ; j < ( 1 << bits ) ; j++ ){
                    int pos = this.secondaryHashTable[ secondaryIndex++ ] - this.DictionarySize;
                    this.dummy[ dummyIndex++ ] = ( 0 <= pos ? pos : -1 );
                }
            }
            this.primaryCount[i] = 0;
        }
        int[] temp = this.secondaryHashTable;
        this.secondaryHashTable = this.dummy;
        this.dummy = temp;

        //------------------------------------------------------------------
        //  �A�����X�g���X�V
        for( int i = 0 ; i < this.prev.length ; i++  ){
            int pos =  this.prev[i] - this.DictionarySize;
            this.prev[i] = ( 0 <= pos ? pos : -1 );
        }
    }

    /**
     * put() �� LzssSearchMethod�Ƀf�[�^��
     * �o�^����Ƃ��Ɏg�p�����f�[�^�ʂ𓾂�B
     * TwoLevelHashSearch �ł́A�����Ŏg�p���Ă��� HashMethod �̎����� 
     * hash() �̂��߂ɕK�v�Ƃ���f�[�^��( HashMethod.hashRequires() �̖߂�l ) 
     * �� ��i�ڂ̃n�b�V���ɕK�v�ȍő�̃o�C�g���𑫂������̂�Ԃ��B
     * 
     * @return ��i�ڂƓ�i�ڂ̃n�b�V���ɕK�v�ȃo�C�g���𑫂������́B
     */
    public int putRequires(){
        return this.primaryHash.hashRequires() 
               + Math.max( Bits.len( this.DictionarySize ) - 5, 0 ) / 2;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  secondary hash method
    //------------------------------------------------------------------
    //  private int secondaryHash( int position, int hashRequires )
    //------------------------------------------------------------------
    /**
     * ��i�ڂ̃n�b�V���֐�
     * 
     * @param position     TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     * @param hashRequires ��i�ڂ̃n�b�V���l���Z�o����̂ɕK�v�ȃo�C�g��
     */
    private int secondaryHash( int position, int hashRequires ){
        int hash = 0;
        int pos  = position + this.primaryHash.hashRequires();

        while( 0 < hashRequires-- ){
            hash <<= 2;
            hash  |= this.TextBuffer[ pos++ ] & 0x03;
        }

        return hash;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  divide and merge chained list
    //------------------------------------------------------------------
    //  private void divide( int dbase, int sbase, int divoff )
    //  private void merge( int dbase, int sbase )
    //------------------------------------------------------------------
    /**
     * ��i�ڂ̃n�b�V���e�[�u���ƘA�����X�g�𕪊򂳂���B
     * 
     * @param dbase  ����� this.dummy �� index
     * @param sbase  ���� this.secondaryHashTable �� index
     * @param divoff ����ʒu 
     */
    private void divide( int dbase, int sbase, int divoff ){
        int limit     = this.DictionarySize;
        int position  = this.secondaryHashTable[ sbase ];
        int[] current = { -1, -1, -1, -1 };
        
        //------------------------------------------------------------------
        //  �A�����X�g�𕪊򂳂��Ă������[�v
        while( limit < position ){
            int shash = this.TextBuffer[ position + divoff ] & 0x03;
            if( 0 < current[ shash ] ){
                this.prev[ current[ shash ] & ( this.DictionarySize - 1 ) ] = position;
            }else{
                this.dummy[ dbase + shash ] = position - this.DictionarySize; 
            }
            current[ shash ] = position;
            position = this.prev[ position & ( this.DictionarySize - 1 ) ];
        }

        //------------------------------------------------------------------
        //  �A�����X�g���^�[�~�l�[�g����B
        for( int i = 0 ; i < current.length ; i++ ){
            if( 0 < current[ i ] ){
                this.prev[ current[ i ] & ( this.DictionarySize - 1 ) ] = -1;
            }else{
                this.dummy[ dbase + i ] = -1; 
            }
        }
    }

    /**
     * ��i�ڂ̃n�b�V���e�[�u���ƘA�����X�g�𑩂˂�B
     * 
     * @param dbase  ����� this.dummy �� index
     * @param sbase  ���� this.secondaryHashTable �� index
     */
    private void merge( int dbase, int sbase ){
        int limit    = this.DictionarySize;
        int position = -1;

        //------------------------------------------------------------------
        //  �A�����X�g�𑩂˂Ă������[�v
        while( true ){
            int shash = 0;
            int max   = this.secondaryHashTable[ sbase ];
            for( int i = 1 ; i < 4 ; i++ ){
                if( max < this.secondaryHashTable[ sbase + i ] ){
                    shash = i;
                    max   = this.secondaryHashTable[ sbase + i ];
                }
            }
            
            if( limit < max ){
                this.secondaryHashTable[ sbase + shash ] = 
                                 this.prev[ max & ( this.DictionarySize - 1 ) ];

                if( 0 < position ){
                    this.prev[ position & ( this.DictionarySize - 1 ) ] = max;
                }else{
                    this.dummy[ dbase ]  = max - this.DictionarySize;
                }
                position = max;
            }else{
                break;
            }
        }

        //------------------------------------------------------------------
        //  �A�����X�g���^�[�~�l�[�g����B
        if( 0 < position ){
            this.prev[ position & ( this.DictionarySize - 1 ) ] = -1;
        }else{
            this.dummy[ dbase ] = -1;
        }
    }

}
//end of TwoLevelHashSearch.java
