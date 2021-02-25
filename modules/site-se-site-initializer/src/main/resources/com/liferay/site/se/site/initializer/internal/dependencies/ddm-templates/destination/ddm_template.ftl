<div class="component-card text-break">
	<div class="card m-0 overflow-hidden">
		<#if (Image1qfb.getData())?? && Image1qfb.getData() != "">
			<img alt="${Image1qfb.getAttribute("alt")}" class="w-100" data-fileentryid="${Image1qfb.getAttribute("fileEntryId")}" src="${Image1qfb.getData()}" />
		</#if>

		<div class="card-body py-4">
			<h2 class="clearfix">
				${Textbiyy.getData()}
			</h2>

			<div class="clearfix mb-4">
				${Content.getData()}
			</div>

			<a class="link" href="${friendlyURLs[themeDisplay.getLanguageId()]!""}" id="fragment-wxau-04-link">
				Learn More
			</a>
		</div>
	</div>
</div>