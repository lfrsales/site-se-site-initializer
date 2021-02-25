<#if entries?has_content>
	<div class="d-flex flex-wrap">
		<#list entries as curEntry>
			<div class="flex-shrink-0 p-3 w-25">
				${articleService.getContentByClassPK(curEntry.getClassPK()?number)}
			</div>
		</#list>
	</div>
</#if>