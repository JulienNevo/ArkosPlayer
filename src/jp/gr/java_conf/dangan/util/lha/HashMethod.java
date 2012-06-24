//start of HashMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashMethod.java
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


/**
 * �n�b�V���֐���񋟂���C���^�[�t�F�C�X�B<br>
 * <br>
 * �R���X�g���N�^�̌`����
 * <pre>
 * HashMethod( byte[] TextBuffer )
 * 
 * <strong>�p�����[�^:</strong>
 *   TextBuffer     - LZSS���k���{���f�[�^�̓������o�b�t�@
 * </pre>
 * �̂悤�Ȍ`���ɑ��邱�ƁB<br>
 * �܂��A�ǉ��̈������Ƃ肽���ꍇ��
 * <pre>
 * HashMethod( byte[] TextBuffer,
 *             Object ExtraData1,
 *             Object ExtraData2 )
 * </pre>
 * �̂悤�Ȍ`����p����B<br>
 * �Ȃ��A�R���X�g���N�^�̈����`�F�b�N�͒ǉ��̈���������ꍇ�ɂ��čs���΂悢�B
 * 
 * <pre>
 * -- revision history --
 * $Log: HashMethod.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version cotrol
 * [change]
 *     requiredSize() �� hashRequires() �ɖ��O�ύX�B
 *     size() �� tableSize() ���O�ύX�B
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
public interface HashMethod{


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract int hash( int position )
    //  public abstract int hashRequires()
    //  public abstract int tableSize()
    //------------------------------------------------------------------
    /**
     * �n�b�V���֐��B
     * �R���X�g���N�^�œn���ꂽ TextBuffer ����
     * position ����̃f�[�^�p�^���� hash�l�𐶐�����B
     *
     * @param position �f�[�^�p�^���̊J�n�ʒu
     * 
     * @return �n�b�V���l
     */
    public abstract int hash( int position );

    /**
     * �n�b�V���֐���
     * �n�b�V���l�𐶐����邽�߂Ɏg�p����o�C�g���𓾂�B
     * 
     * @return �n�b�V���֐����n�b�V���l��
     *         �������邽�߂Ɏg�p����o�C�g��
     */
    public abstract int hashRequires();

    /**
     * ���� HashMethod ���g�����ꍇ�� 
     * HashTable �̃T�C�Y�𓾂�B
     * 
     * @return ���� HashMethod ���g�����ꍇ�� HashTable �̃T�C�Y
     */
    public abstract int tableSize();

}
//end of HashMethod.java
