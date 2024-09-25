/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RestServiceExceptionTranslator.java
 *  <p>
 *  Licensed under the Apache License Version 2.0;
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.konggradio.core.log.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.launch.constant.EnvConstant;
import org.konggradio.core.log.config.KongGradioLogToolAutoConfiguration;
import org.konggradio.core.log.exception.ServiceException;
import org.konggradio.core.secure.exception.SecureException;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.tool.api.ResultCode;
import org.konggradio.core.tool.utils.Func;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.Servlet;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理，处理可预见的异常
 *
 * @author ydlian
 */
//@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice
public class RestServiceExceptionTranslator {
	private final Log log = LogFactory.getLog(EnvConstant.class);

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(MissingServletRequestParameterException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.warn("缺少请求参数"+e.getMessage());
		String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
		return R.fail(ResultCode.PARAM_MISS, message);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(MethodArgumentTypeMismatchException e) {
		log.warn("请求参数格式错误"+e.getMessage());
		String message = String.format("请求参数格式错误: %s", e.getName());
		return R.fail(ResultCode.PARAM_TYPE_ERROR, message);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(MethodArgumentNotValidException e) {
		log.warn("参数验证失败"+e.getMessage());
		return handleError(e.getBindingResult());
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(BindException e) {
		log.warn("参数绑定失败"+ e.getMessage());
		return handleError(e.getBindingResult());
	}

	private R handleError(BindingResult result) {
		FieldError error = result.getFieldError();
		String message = String.format("%s:%s", error.getField(), error.getDefaultMessage());
		return R.fail(ResultCode.PARAM_BIND_ERROR, message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(ConstraintViolationException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.warn("参数验证失败"+ e.getMessage());
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		ConstraintViolation<?> violation = violations.iterator().next();
		String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
		String message = String.format("%s:%s", path, violation.getMessage());
		return R.fail(ResultCode.PARAM_VALID_ERROR, message);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public R handleError(NoHandlerFoundException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.error("404没找到请求:{}"+e.getMessage());
		return R.fail(ResultCode.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(HttpMessageNotReadableException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.error("消息不能读取:{}"+ e.getMessage());
		return R.fail(ResultCode.MSG_NOT_READABLE, e.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public R handleError(HttpRequestMethodNotSupportedException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.error("不支持当前请求方法:{}"+ e.getMessage());
		return R.fail(ResultCode.METHOD_NOT_SUPPORTED, e.getMessage());
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public R handleError(HttpMediaTypeNotSupportedException e) {
		log.error("不支持当前媒体类型:{}"+ e.getMessage());
		return R.fail(ResultCode.MEDIA_TYPE_NOT_SUPPORTED, e.getMessage());
	}

	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleError(ServiceException e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.error("业务异常", e);
		return R.fail(e.getResultCode(), e.getMessage());
	}

	@ExceptionHandler(SecureException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public R handleError(SecureException e) {
		log.error("认证异常", e);
		return R.fail(e.getResultCode(), e.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R handleError(Throwable e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		log.error("服务器异常", e);
		//发送服务异常事件
		//ErrorLogPublisher.publishEvent(e, UrlUtil.getPath(WebUtil.getRequest().getRequestURI()));
		return R.fail(ResultCode.INTERNAL_SERVER_ERROR, (Func.isEmpty(e.getMessage()) ? ResultCode.INTERNAL_SERVER_ERROR.getMessage() : e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public R baseExceptionHandler(Exception e) {
		if (!KongGradioLogToolAutoConfiguration.getDebugSwitch() || !ApplicationRegister.getApplicationLcRegStatus()) {
			return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		}
		R result = R.fail(ResultCode.INTERNAL_SERVER_ERROR);
		log.error("==application Exception error==", e);
		return result;
	}
}
