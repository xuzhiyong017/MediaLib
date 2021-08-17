package com.sky.media.image

import android.opengl.GLES20
import com.sky.media.image.core.util.LogUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午2:35
 * @Email: 18971269648@163.com
 * @description:
 */

const val TAG = "GLRender"
abstract class GLRender {

    private val FLOAT_SIZE_BYTES = 4 //一个float数据占四个字节
    private val TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES //每2个元素表示一个顶点
    val ATTRIBUTE_POSITION = "position"
    val ATTRIBUTE_TEXCOORD = "inputTextureCoordinate"
    val UNIFORM_TEXTURE0 = "inputImageTexture"
    protected val UNIFORM_TEXTUREBASE = "inputImageTexture"
    val VARYING_TEXCOORD = "textureCoordinate"

    protected var curRotation = 0
    protected var texture_in = 0
    open var customSizeSet = false
    private var initialized = false
    private var sizeChanged = false
    protected var renderVertices: FloatBuffer? = null
    protected var textureVertices = arrayOfNulls<FloatBuffer>(4)
    protected var mRunOnDraw: Queue<Runnable> = LinkedList<Runnable>()

    private var width = 0
    private var height = 0

    protected var positionHandle = 0
    protected var texCoordHandle = 0
    protected open var textureHandle = 0
    open var programHandle = 0

    private var vertexShaderHandle = 0
    private var fragmentShaderHandle = 0
    open var mFragmentShader = """
        precision mediump float;
        uniform sampler2D inputImageTexture;
        varying vec2 textureCoordinate;
        void main(){
           gl_FragColor = texture2D(inputImageTexture,textureCoordinate);
        }
   
     """.trimIndent()
    open var mVertexShader = """
         attribute vec4 position;
         attribute vec2 inputTextureCoordinate;
         varying vec2 textureCoordinate;
         void main() {
           textureCoordinate = inputTextureCoordinate;
           gl_Position = position;
         }
         
       """.trimIndent()


    protected open var alpha = 0.0f
    protected var red = 0.0f
    protected var green = 0.0f
    protected var blue = 0.0f


    init {
        setRenderVertices(floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f)
        )

        var textureCoord = floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f)


        textureVertices[0] = ByteBuffer.allocateDirect(textureCoord.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[0]?.put(textureCoord)?.position(0)

        textureCoord = floatArrayOf(
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f)
        textureVertices[1] = ByteBuffer.allocateDirect(textureCoord.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[1]?.put(textureCoord)?.position(0)

        textureCoord = floatArrayOf(
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f)
        textureVertices[2] = ByteBuffer.allocateDirect(textureCoord.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[2]?.put(textureCoord)?.position(0)

        textureCoord = floatArrayOf(
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f)
        textureVertices[3] = ByteBuffer.allocateDirect(textureCoord.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[3]?.put(textureCoord)?.position(0)

        curRotation = 0
        texture_in = 0
        customSizeSet = false
        initialized = false
        sizeChanged = false
    }

    protected open fun setRenderVertices(fArr: FloatArray) {
        renderVertices = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        renderVertices?.put(fArr)?.position(0)
    }

    protected fun runAll(queue: Queue<Runnable>){
        synchronized(queue){
            while (!queue.isEmpty()){
                queue.poll().run()
            }
        }
    }

    protected fun runOnDraw(runnable: Runnable){
        synchronized(mRunOnDraw){
            mRunOnDraw.add(runnable)
        }
    }

    open fun getRotate90Degrees(): Int {
        return curRotation
    }

    open fun resetRotate(): Boolean {
        if (curRotation % 2 == 1) {
            swapWidthAndHeight()
            curRotation = 0
            return true
        }
        curRotation = 0
        return false
    }

    open fun setRotate90Degrees(i: Int) {
        var i = i
        while (i < 0) {
            i += 4
        }
        curRotation += i
        curRotation %= 4
        if (i % 2 == 1) {
            swapWidthAndHeight()
        }
    }

    open fun setWidth(i: Int) {
        if (!customSizeSet && width != i) {
            width = i
            sizeChanged = true
        }
    }

    open fun setHeight(i: Int) {
        if (!customSizeSet && height != i) {
            height = i
            sizeChanged = true
        }
    }

    open fun getWidth(): Int {
        return width
    }

    open fun getHeight(): Int {
        return height
    }


    open fun setRenderSize(renderWidth: Int, renderHeight: Int) {
        customSizeSet = true
        this.width = renderWidth
        this.height = renderHeight
        sizeChanged = true
    }

    open fun swapWidthAndHeight() {
        val tem = width
        width = height
        height = tem
        sizeChanged = true
    }

    open fun bindShaderValues(){
        renderVertices?.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle,2,GLES20.GL_FLOAT,false,2 * 4,renderVertices)
        textureVertices[curRotation]?.position(0)
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle,2,GLES20.GL_FLOAT,false,2 * 4,textureVertices[curRotation])
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        checkEGLError("glActiveTexture")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture_in)
        checkEGLError("glBindTexture")
        GLES20.glUniform1i(textureHandle,0)
        checkEGLError("textureHandle")
    }

    protected open fun bindShaderAttributes() {
        GLES20.glBindAttribLocation(programHandle, 0, ATTRIBUTE_POSITION)
        GLES20.glBindAttribLocation(programHandle, 1, ATTRIBUTE_TEXCOORD)
    }

    protected open fun initShaderHandles() {
        textureHandle = GLES20.glGetUniformLocation(programHandle, "inputImageTexture")
        positionHandle = GLES20.glGetAttribLocation(programHandle, "position")
        texCoordHandle = GLES20.glGetAttribLocation(programHandle, "inputTextureCoordinate")
    }

    open fun reInitialize() {
        initialized = false
    }

    open fun onDrawFrame() {
        if (!initialized) {
            initWithGLContext()
            initialized = true
        }
        if (sizeChanged) {
            handleSizeChange()
            sizeChanged = false
        }
        runAll(mRunOnDraw)
        drawFrame()
    }

    open fun initWithGLContext() {
        var iArr = IntArray(1)
        var message = "none"
        vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        if(vertexShaderHandle != 0){
            GLES20.glShaderSource(vertexShaderHandle,mVertexShader)
            GLES20.glCompileShader(vertexShaderHandle)
            GLES20.glGetShaderiv(vertexShaderHandle,GLES20.GL_COMPILE_STATUS,iArr,0)
            if(iArr[0] == 0){
                message = GLES20.glGetShaderInfoLog(vertexShaderHandle)
                GLES20.glDeleteShader(vertexShaderHandle)
                vertexShaderHandle = 0
            }
        }

        if(vertexShaderHandle == 0){
            throw RuntimeException("$this: Could not create vertex shader. Reason: $message")
        }

        fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        if(fragmentShaderHandle != 0){
            GLES20.glShaderSource(fragmentShaderHandle,mFragmentShader)
            GLES20.glCompileShader(fragmentShaderHandle)
            iArr = IntArray(1)
            GLES20.glGetShaderiv(fragmentShaderHandle,GLES20.GL_COMPILE_STATUS,iArr,0)
            if(iArr[0] == 0){
                message = GLES20.glGetShaderInfoLog(fragmentShaderHandle)
                GLES20.glDeleteShader(fragmentShaderHandle)
                fragmentShaderHandle = 0
            }
        }

        if(fragmentShaderHandle == 0){
            throw RuntimeException("$this: Could not create fragment shader. Reason: $message")
        }

        programHandle = GLES20.glCreateProgram()
        if(programHandle != 0){
            GLES20.glAttachShader(programHandle,vertexShaderHandle)
            GLES20.glAttachShader(programHandle,fragmentShaderHandle)
            bindShaderAttributes()
            GLES20.glLinkProgram(programHandle)
            var iArr2 = IntArray(1)
            GLES20.glGetProgramiv(programHandle,GLES20.GL_LINK_STATUS,iArr2,0)
            if(iArr2[0] == 0){
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }

        if(programHandle == 0){
            throw RuntimeException("Could not create program.")
        }
        initShaderHandles()
    }

    protected open fun handleSizeChange() {}

    open fun drawFrame(){
        if(texture_in != 0){
            if(!(width == 0 || height == 0)){
                GLES20.glViewport(0,0,width,height)
            }
            GLES20.glUseProgram(programHandle)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            GLES20.glClearColor(
                getBackgroundRed(),
                getBackgroundGreen(),
                getBackgroundBlue(),
                getBackgroundAlpha()
            )
            bindShaderValues()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)

            //禁止顶点数组的句柄
            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(texCoordHandle)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0)
            GLES20.glUseProgram(0)
        }
    }

    open fun destroy() {
        initialized = false
        if (programHandle != 0) {
            GLES20.glDeleteProgram(programHandle)
            programHandle = 0
        }
        if (vertexShaderHandle != 0) {
            GLES20.glDeleteShader(vertexShaderHandle)
            vertexShaderHandle = 0
        }
        if (fragmentShaderHandle != 0) {
            GLES20.glDeleteShader(fragmentShaderHandle)
            fragmentShaderHandle = 0
        }
    }

    open fun getBackgroundRed(): Float {
        return this.red
    }

    open fun setBackgroundRed(f: Float) {
        this.red = f
    }

    open fun getBackgroundGreen(): Float {
        return this.green
    }

    open fun setBackgroundGreen(f: Float) {
        this.green = f
    }

    open fun getBackgroundBlue(): Float {
        return this.blue
    }

    open fun setBackgroundBlue(f: Float) {
        this.blue = f
    }

    open fun getBackgroundAlpha(): Float {
        return this.alpha
    }

    open fun setBackgroundAlpha(f: Float) {
        this.alpha = f
    }

    open fun checkEGLError(str: String) {
//        val eglGetError = GLES20.glGetError()
//        if (eglGetError != GLES20.GL_NO_ERROR) {
//            throw RuntimeException("$str: GLES20 error: 0x" + Integer.toHexString(eglGetError) + " $this")
//        }
    }
}