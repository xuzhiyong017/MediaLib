package com.sky.media.kit.render.videorender

import com.sky.media.image.core.base.BaseRender

class MirrorRender : BaseRender() {

    override var mFragmentShader: String = """precision highp float;
                                            varying lowp vec2 textureCoordinate;
                                            uniform sampler2D inputImageTexture;
                                            void main(){
                                                float gate = 0.01;
                                                if(textureCoordinate.x < 0.5-gate){
                                                    gl_FragColor = texture2D(inputImageTexture,textureCoordinate);
                                                }else if(textureCoordinate.x < 0.5+gate){ 
                                                    float weight = (textureCoordinate.x + gate - 0.5) / (2.0 * gate);
                                                    vec4 color1 = texture2D(inputImageTexture,textureCoordinate);
                                                    vec4 color2 = texture2D(inputImageTexture,vec2(1.0 - textureCoordinate.x, textureCoordinate.y));
                                                    gl_FragColor = mix(color1, color2, weight);
                                                }else{
                                                    gl_FragColor = texture2D(inputImageTexture,vec2(1.0 - textureCoordinate.x,    textureCoordinate.y));
                                                }
                                            }"""

}