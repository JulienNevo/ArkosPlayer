//start of NotEnoughBitsException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * NotEnoughBitsException.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions
import java.io.IOException;

/**
 * �v�����ꂽ�r�b�g���̃f�[�^�𓾂��Ȃ������ꍇ��
 * ���������O�B<br>
 * BitDataBrokenException �ƈႢ�A������̗�O��
 * ������ꍇ�ɂ� ���ۂɂ͓ǂݍ��ݓ�����s���Ă�
 * �Ȃ����߁A�ǂݍ��݈ʒu�͗�O�𓊂���O�̎��_��
 * �����ł���_�ɒ��ӂ��邱�ƁB<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: NotEnoughBitsException.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class NotEnoughBitsException extends IOException{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int availableBits
    //------------------------------------------------------------------
    /**
     * ���ۂɓǂݍ��߂�r�b�g��
     */
    private int availableBits;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private NotEnoughBitsException()
    //  public NotEnoughBitsException( int availableBits )
    //  public NotEnoughBitsException( String message, int availableBits )
    //------------------------------------------------------------------
    /**
     * �f�t�H���g�R���X�g���N�^�B
     * �g�p�s�B
     */
    private NotEnoughBitsException(){ }

    /**
     * availableBits �g�p�\�ł��邱�Ƃ�����
     * NotEnoughBitsException ���\�z����B
     * 
     * @param availableBits �g�p�\�ȃr�b�g��
     */
    public NotEnoughBitsException( int availableBits ){
        super();
        this.availableBits = availableBits;
    }

    /**
     * availableBits �g�p�\�ł��邱�Ƃ������A
     * �ڍׂȃ��b�Z�[�W������
     * NotEnoughBitsException ���\�z����B
     * 
     * @param message       �ڍׂȃ��b�Z�[�W
     * @param availableBits �g�p�\�ȃr�b�g��
     */
    public NotEnoughBitsException( String message, int availableBits ){
        super( message );
        this.availableBits = availableBits;
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public int getAvailableBits()
    //------------------------------------------------------------------
    /**
     * �g�p�\�ȃr�b�g���𓾂�B<br>
     * ���̗�O�𓊂������\�b�h�ɂ����āA���ݎg�p�\�ȃr�b�g����Ԃ��B<br>
     * 
     * @return �g�p�\�ȃr�b�g��
     */
    public int getAvailableBits(){
        return this.availableBits;
    }

}
//end of NotEnoughBitsException.java
