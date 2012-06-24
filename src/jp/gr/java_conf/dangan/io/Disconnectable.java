//start of Disconnectable.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Disconnectable.java
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
 * �ڑ��������ł���X�g���[���̂��߂�
 * �C���^�[�t�F�C�X�B<br>
 * ���̃C���^�[�t�F�C�X����������X�g���[����
 * close() �� disconnect() ���ĂԂׂ��ł���B<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: Disconnectable.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *     �\�[�X����
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface Disconnectable{

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract void disconnect()
    //------------------------------------------------------------------
    /**
     * �ڑ�����Ă����X�g���[���Ƃ̐ڑ�����������B
     * 
     * @exception IOException ���o�̓G���[�����������ꍇ
     */
    public abstract void disconnect() throws IOException;

}
//end of Disconnectable.java
