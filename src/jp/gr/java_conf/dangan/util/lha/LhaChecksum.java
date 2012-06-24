//start of LhaChecksum.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaChecksum.java
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
 * LHA�Ŏg�p����� �P���� 1�o�C�g�̃`�F�b�N�T���l��
 * �Z�o���邽�߂̃N���X�B
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaChecksum.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [maintanance]
 *     �\�[�X����
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class LhaChecksum implements Checksum{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int checksum
    //------------------------------------------------------------------
    /** 
     * �`�F�b�N�T���l
     */
    private int checksum;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public LhaChecksum()
    //------------------------------------------------------------------
    /**
     * �V���� �`�F�b�N�T���N���X���쐬����B
     */
    public LhaChecksum(){
        super();
        this.reset();
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum method
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * byte8 �Ŏw�肵�� 1�o�C�g�̃f�[�^�� �`�F�b�N�T���l���X�V����B
     *
     * @param byte8 �`�F�b�N�T�����X�V����1�o�C�g�̃f�[�^
     */
    public void update( int byte8 ){
        this.checksum += byte8;
    }

    /**
     * buffer �Ŏw�肵���o�C�g�z��� �`�F�b�N�T���l���X�V����B
     * ���̃��\�b�h��
     *   update( buffer, 0, buffer.length ) 
     * �Ɠ����B
     * 
     * @param buffer �`�F�b�N�T�����X�V����f�[�^�����o�C�g�z��
     */
    public void update( byte[] buffer ){
        this.update( buffer, 0, buffer.length );
    }

    /**
     * buffer �Ŏw�肵���o�C�g�z��� �`�F�b�N�T���l���X�V����B
     * 
     * @param buffer �`�F�b�N�T�����X�V����f�[�^�����o�C�g�z��
     * @param index  �f�[�^�̊J�n�ʒu
     * @param length �`�F�b�N�T���̍X�V�Ɏg���o�C�g��
     */
    public void update( byte[] buffer, int index, int length ){
        while( 0 < length-- )
            this.checksum += buffer[index++];
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
     * �`�F�b�N�T���l�������l�ɐݒ肵�Ȃ����B
     */
    public void reset(){
        this.checksum = 0;
    }

    /**
     * �`�F�b�N�T���l�𓾂�B
     * �`�F�b�N�T���l�� 1�o�C�g�̒l�ł���A 
     * 0x00�`0xFF�Ƀ}�b�v�����B
     * 
     * @return �`�F�b�N�T���l
     */
    public long getValue(){
        return this.checksum & 0xFF;
    }

}
//end of LhaChecksum.java
