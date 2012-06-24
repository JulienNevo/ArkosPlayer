//start of LzssSearchMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssSearchMethod.java
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
 * LzssOutputStream �Ŏg�p�����
 * �Œ���v������񋟂���C���^�[�t�F�C�X�B<br> 
 * 
 * <br>
 * �R���X�g���N�^�̌`����
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer )
 * 
 * <strong>�p�����[�^:</strong>
 *   DictionarySize - LZSS�̎����T�C�Y
 *   MaxMatch       - LZSS�̍ő��v��
 *   Threshold      - LZSS�̈��k/�񈳏k��臒l
 *   TextBuffer     - LZSS���k���{���f�[�^�̓������o�b�t�@
 * </pre>
 * �̂悤�Ȍ`���ɑ��邱�ƁB<br>
 * �܂��A�ǉ��̈������Ƃ肽���ꍇ��
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer,
 *                   Object ExtraArgument1,
 *                   Object ExtraArgument2 )
 * </pre>
 * �̂悤�Ȍ`����p����B<br>
 * �Ȃ��A�R���X�g���N�^�̈����`�F�b�N�͒ǉ��̈���������ꍇ�ɂ��čs���΂悢�B
 * <br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LzssSearchMethod.java,v $
 * Revision 1.1  2002/12/04 00:00:00  dangan
 * [maintenance]
 *     �\�[�X����
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     slide() �ň�������炸�� 
 *     �X���C�h������� DictionarySize �Ƃ���悤�ɕύX�B
 *     putLength �� putRequires �ɕύX
 * [maintenance]
 *     �^�u�p�~
 *     ���C�Z���X���̏C��
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public interface LzssSearchMethod{

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract void put( int position )
    //  public abstract int searchAndPut( int position )
    //  public abstract int search( int position, int lastPutPos )
    //  public abstract void slide()
    //  public abstract int putRequires()
    //------------------------------------------------------------------
    /**
     * position ����n�܂�f�[�^�p�^���� 
     * LzssSearchMethod �̎������@�\�ɓo�^����B<br>
     * LzssOutputStream �� ���`�ɁA�d�������A
     * put �܂��� searchAndPut ���Ăяo���B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     */
    public abstract void put( int position );

    /**
     * �����@�\�ɓo�^���ꂽ�f�[�^�p�^������
     * position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂��������A
     * ������ position ����n�܂�f�[�^�p�^���� 
     * LzssSearchMethod �̎������@�\�ɓo�^����B<br>
     * LzssOutputStream �� ���`�ɁA�d�������A
     * put �܂��� searchAndPut ���Ăяo���B<br>
     * 
     * @param position TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
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
    public abstract int searchAndPut( int position );

    /**
     * �����@�\�ɓo�^���ꂽ�f�[�^�p�^������
     * position ����n�܂�f�[�^�p�^����
     * �Œ��̈�v�������̂���������B<br>
     * ���̃��\�b�h�� LzssOutputStream �� 
     * flush() ���������邽�߂����ɒ񋟂����B<br>
     * TextBuffer.length &lt position + MaxMatch �ƂȂ�悤�� 
     * position �ɂ��Ή����邱�ƁB
     * 
     * @param position   TextBuffer���̃f�[�^�p�^���̊J�n�ʒu
     * @param lastPutPos �Ō�ɓo�^�����f�[�^�p�^���̊J�n�ʒu
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
    public abstract int search( int position, int lastPutPos );

    /**
     * LzssOutputStream �� slide() ��TextBuffer���̃f�[�^��
     * DictionarySize �����ړ�������ۂɌ����@�\���̃f�[�^��
     * �����Ɩ��������ړ������鏈�����s���B
     */
    public abstract void slide();

    /**
     * put() �܂��� searchAndPut() ���g�p����
     * �f�[�^�p�^���������@�\�ɓo�^���鎞��
     * �K�v�Ƃ���f�[�^�ʂ𓾂�B<br>
     * 
     * @return put() �܂��� searchAndPut() ��
     *         �����@�\�ɓo�^����f�[�^��
     */
    public abstract int putRequires();

}
//end of LzssSearchMethod.java
