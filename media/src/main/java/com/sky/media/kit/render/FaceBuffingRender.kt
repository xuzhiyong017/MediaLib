package com.sky.media.kit.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午6:00
 * @Email: 18971269648@163.com
 * @description:
 */
class FaceBuffingRender : BaseRender(), IAdjustable {
    private val UNIFORM_BUFFING_LEVEL = "u_mix"
    private var mBuffingLevel = 0f
    private var mBuffingLevelHandle = 0

    init {
        mFragmentShader = """#define x_a 640.0
                            #define y_a 1136.0
                            precision lowp float;
                            uniform sampler2D inputImageTexture;
                            varying lowp vec2 textureCoordinate;
                            uniform lowp float u_mix;
                            void main(){
                                highp vec4 fragColor;
                                if (u_mix <= 0.1){
                                    fragColor = texture2D(inputImageTexture, textureCoordinate);
                                }else {
                                    vec3 centralColor;
                                    float mul_x = 1.6 / x_a;
                                    float mul_y = 1.6 / y_a;
                                    vec2 blurCoordinates0 = textureCoordinate + vec2(0.0 * mul_x,-10.0 * mul_y);
                                    vec2 blurCoordinates1 = textureCoordinate + vec2(5.0 * mul_x,-8.0 * mul_y);
                                    vec2 blurCoordinates2 = textureCoordinate + vec2(8.0 * mul_x,-5.0 * mul_y);
                                    vec2 blurCoordinates3 = textureCoordinate + vec2(10.0 * mul_x,0.0 * mul_y);
                                    vec2 blurCoordinates4 = textureCoordinate + vec2(8.0 * mul_x,5.0 * mul_y);
                                    vec2 blurCoordinates5 = textureCoordinate + vec2(5.0 * mul_x,8.0 * mul_y);
                                    vec2 blurCoordinates6 = textureCoordinate + vec2(0.0 * mul_x,10.0 * mul_y);
                                    vec2 blurCoordinates7 = textureCoordinate + vec2(-5.0 * mul_x,8.0 * mul_y);
                                    vec2 blurCoordinates8 = textureCoordinate + vec2(-8.0 * mul_x,5.0 * mul_y);
                                    vec2 blurCoordinates9 = textureCoordinate + vec2(-10.0 * mul_x,0.0 * mul_y);
                                    vec2 blurCoordinates10 = textureCoordinate + vec2(-8.0 * mul_x,-5.0 * mul_y);
                                    vec2 blurCoordinates11 = textureCoordinate + vec2(-5.0 * mul_x,-8.0 * mul_y);
                                            
                                    float central;
                                    float gaussianWeightTotal;
                                    float sum;
                                    float sample;
                                    float distanceFromCentralColor;
                                    float gaussianWeight;
                                    float distanceNormalizationFactor = 3.6;
                                    central = texture2D(inputImageTexture, textureCoordinate).g;
                                    gaussianWeightTotal = 0.2;
                                    sum = central * 0.2;
                                    sample = texture2D(inputImageTexture, blurCoordinates0).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates1).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates2).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates3).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates4).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates5).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates6).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates7).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates8).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates9).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates10).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sample = texture2D(inputImageTexture, blurCoordinates11).g;
                                    distanceFromCentralColor = min(abs(central - sample) * distanceNormalizationFactor, 1.0);
                                    gaussianWeight = 0.08 * (1.0 - distanceFromCentralColor);
                                    gaussianWeightTotal += gaussianWeight;
                                    sum += sample * gaussianWeight;
                                    sum = sum/gaussianWeightTotal;
                                    centralColor = texture2D(inputImageTexture, textureCoordinate).rgb;
                            
                                    sample = centralColor.g - sum + 0.5;
                                    for(int i = 0; i < 5; ++i){
                                        if(sample <= 0.5) {
                                            sample = sample * sample * 2.0;
                                        }
                                        else{
                                            sample = 1.0 - ((1.0 - sample)*(1.0 - sample) * 2.0);
                                        }
                                    }
                                    float aa = 1.0 + pow(sum, 0.3)*0.07;
                                    vec3 smoothColor = centralColor*aa - vec3(sample)*(aa-1.0);// get smooth color
                                    smoothColor = clamp(smoothColor,vec3(0.0),vec3(1.0));//make smooth color right
                                                     
                                    smoothColor = mix(centralColor, smoothColor, pow(centralColor.g, 0.33));
                                    smoothColor = mix(centralColor, smoothColor, pow(centralColor.g, 0.39));
                            
                                    smoothColor = mix(centralColor, smoothColor, u_mix);
                                    fragColor = vec4(pow(smoothColor, vec3(0.96)),1.0);
                                }
                                gl_FragColor = fragColor;
                            }
                            """
    }
     override fun initShaderHandles() {
        super.initShaderHandles()
        mBuffingLevelHandle = GLES20.glGetUniformLocation(this.programHandle, UNIFORM_BUFFING_LEVEL)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mBuffingLevelHandle, mBuffingLevel)
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mBuffingLevel = (i - i2) * 1.0f / (i3 - i2)
    }
}