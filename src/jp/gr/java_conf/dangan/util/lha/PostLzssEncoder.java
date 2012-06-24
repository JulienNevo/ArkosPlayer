//start of PostLzssEncoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLzssEncoder.java
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

//import exceptions
import java.io.IOException;

/**
 * LZSS���k�R�[�h���������� �C���^�[�t�F�C�X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLzssEncoder.java,v $
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface PostLzssEncoder{


    //------------------------------------------------------------------
    //  original method ( on the model of java.io.OutputStream )
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public abstract void flush()
    //  public abstract void close()
    //------------------------------------------------------------------
    /**
     * ���� PostLzssEncoder �Ƀo�b�t�@�����O����Ă���
     * �o�͉\�ȃf�[�^���o�͐�� OutputStream �ɏo�͂��A
     * �o�͐�� OutputStream �� flush() ����B<br>
     * java.io.OutputStream �� ���\�b�h flush() �Ǝ��Ă��邪�A
     * flush() ���Ȃ������ꍇ�� flush() �����ꍇ�̏o�͂ɂ��Ă�
     * �����ł��邱�Ƃ�ۏ؂��Ȃ��ėǂ��B<br>
     * �܂�OutputStream �� flush() �ł͓����f�[�^���o�͂��鎖��
     * ���҂����悤�Ȉȉ��̓�̃R�[�h�́A
     * PostLzssEncoder �ɂ����Ă� �ʂ̃f�[�^���o�͂����Ă��ǂ��B
     * <pre>
     * (1)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.close();
     * 
     * (2)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.close();
     * </pre>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void flush() throws IOException;

    /**
     * ���̏o�̓X�g���[���ƁA�ڑ����ꂽ�o�̓X�g���[������A
     * �g�p���Ă������\�[�X���J������B<br>
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void close() throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public abstract void writeCode( int code )
    //  public abstract void writeOffset( int offset )
    //------------------------------------------------------------------
    /**
     * 1byte �� LZSS�����k�̃f�[�^�������́A
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�����������ށB<br>
     * �����k�f�[�^�� 0�`255�A
     * LZSS���k�R�[�h(��v��)�� 256�`510 ���g�p���邱�ƁB
     * 
     * @param code 1byte �� LZSS�����k�̃f�[�^�������́A
     *             LZSS �ň��k���ꂽ���k�R�[�h�̂�����v��
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void writeCode( int code ) throws IOException;

    /**
     * LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu���������ށB<br>
     * 
     * @param offset LZSS �ň��k���ꂽ���k�R�[�h�̂�����v�ʒu
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void writeOffset( int offset ) throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public abstract int getDictionarySize()
    //  public abstract int getMaxMatch()
    //  public abstract int getThreshold()
    //------------------------------------------------------------------
    /**
     * ����PostLzssEncoder����������LZSS�����̃T�C�Y�𓾂�B
     * 
     * @param LZSS�����̃T�C�Y
     */
    public abstract int getDictionarySize();

    /**
     * ����PostLzssEncoder����������ő��v���𓾂�B
     * 
     * @param �Œ���v��
     */
    public abstract int getMaxMatch();

    /**
     * ����PostLzssEncoder���������鈳�k�A�񈳏k��臒l�𓾂�B
     * 
     * @param ���k�A�񈳏k��臒l
     */
    public abstract int getThreshold();

}
//end of PostLzssEncoder.java
