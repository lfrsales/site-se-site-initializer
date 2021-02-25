<#if entries?has_content>
	<#assign languageId = localeUtil.toLanguageId(locale) />

	<style>
		.taglib-language-option {
			background: none no-repeat 5px center;
			padding-left: 25px;
		}

		.language-selector .input-select-wrapper select.form-control {
			background: transparent;
			background-image: none;
			border-width: 0;
			color: inherit;
			padding: 4px;
			text-transform: uppercase;
		}
	</style>

	<@liferay_aui["form"]
		action=formAction
		cssClass="language-selector"
		method="post"
		name='${namespace + formName}'
		useNamespace=false
	>
		<@liferay_aui["select"]
			changesContext=true
			id='${namespace + formName}'
			label=""
			name='${name}'
			onChange='${namespace + "changeLanguage();"}'
			title="language"
		>
			<#list entries as entry>
				<@liferay_aui["option"]
					cssClass="taglib-language-option taglib-language-option-${entry.getW3cLanguageId()}"
					disabled=entry.isDisabled()
					label=entry.getLongDisplayName()
					lang=entry.getW3cLanguageId()
					localizeLabel=false
					selected=entry.isSelected()
					value=entry.getLanguageId()
				/>
			</#list>
		</@>
	</@>

	<@liferay_aui["script"]>
		function ${namespace}changeLanguage() {
			submitForm(document.${namespace + formName});
		}
	</@>
</#if>