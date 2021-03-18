<!-- Add jQuery library (required) -->

<script src="https://cdn.jsdelivr.net/npm/jquery@3.4.1/dist/jquery.min.js"></script>

<!-- Add the evo-calendar.js for.. obviously, functionality! -->

<script src="https://cdn.jsdelivr.net/npm/evo-calendar@1.1.2/evo-calendar/js/evo-calendar.min.js"></script>
<!-- Add the evo-calendar.css for styling -->

<link href="https://cdn.jsdelivr.net/npm/evo-calendar@1.1.0/evo-calendar/css/evo-calendar.min.css" rel="stylesheet" type="text/css" />

<style>
	.borders
	{
		border:solid;
		background: var(--primary);
		border-color:white;

	}
	.events {
		position: absolute;
		z-index: 199;
		top: 0;
		width: 100%;
		height: 100%;
		background: #808080cf;
		display: none;
		max-height: 100%;
		left:0;
		right:0;

	}
	.events .card
	{
		height: 100%;
		margin-top: auto;
		margin-bottom: auto;
		width: 100%;
		margin: auto;
		background: transparent;
		border: solid;
		border-width: thin;
		border-color: white;
	}
	.events-contents .card
	{
		height:auto!important;
		max-height:100%;
	}
	.events .card .card-body
	{
		overflow:scroll;
	}

	.header-title {
		display: flex;
	}

	.middle {
		margin-top: auto!important;
		;
		margin-bottom: auto!important;
	}

	.bg-custom-primary {
		background: var(--primary);
		color: var(--light)
	}

	.events-contents {
		overflow: scroll;
		height: 100%;
	}
	.evo-calendar {
	-webkit-box-shadow: 0 10px 50px -20px var(--primary);
			box-shadow: 0 10px 50px -20px var(--primary);
	}
	.calendar-sidebar
	{
		 background-color: var(--primary);
		  -webkit-box-shadow: 5px 0 18px -3px var(--primary);
			box-shadow: 5px 0 18px -3px var(--primary);
	}
	.calendar-sidebar > span#sidebarToggler {
		 background-color: var(--primary);

	-webkit-box-shadow: 5px 0 18px -3px var(--primary);
			box-shadow: 5px 0 18px -3px var(--primary);
	}
	#eventListToggler {
		background-color: var(--primary);
		-webkit-box-shadow: 5px 0 18px -3px var(--primary);
			box-shadow: 5px 0 18px -3px var(--primary);
	}
	.event-list > .event-empty {
		border: 1px solid var(--primary);
	}
	.event-list > .event-empty > p {
		color: var(--primary);
	}
	.calendar-sidebar > .calendar-year {
		background-color: var(--primary);
	}
	.calendar-sidebar > .month-list {
		background-color: var(--primary);
	}
	.month.active-month
	{
		background-color:var(--light)!important;
		color:var(--primary)!important;
	}
		.month:hover
	{
		background-color:var(--light)!important;
		color:var(--primary)!important;
	}
</style>

<script>
	var _calendarEvents = [];
</script>
<#assign flag = false />
<#if entries?has_content>
	<!-- en_US -->

	<#list entries as curEntry>
		<#assign
			renderer = curEntry.getAssetRenderer()
			className = renderer.getClassName()
		/>

		<#if className !="com.liferay.calendar.model.CalendarBooking">
			<#assign flag = true />
			<#break>
		</#if>
		<#assign
			assetObject = renderer.getAssetObject()
			title = assetObject.getTitle(locale, true)
			description = assetObject.getDescription(locale, true)! ""
			id = assetObject.getCalendarBookingId()
			StartDate = assetObject.getStartTime()?number_to_datetime
			EndDate = assetObject.getEndTime()?number_to_datetime
			FormattedStartDate = StartDate?string[ "MMMM/d/yyyy"]
			FormattedEndDate = EndDate?string[ "MMMM/d/yyyy"]
		/>

		<script>
			var event = {
				id: '${id}',
				name: `${title}`,
				date: ["${FormattedStartDate}", "${FormattedEndDate}"],
				type: "holiday",
				description: `${description}`
			};
			_calendarEvents.push(event);
			console.log(event);
		</script>
	</#list>
</#if>
<#if flag==false>
<div class="fragment-calendar">
<div class="events">
<div class="card">
<div class="card-header" style="padding:0!important;margin:0">
<div class="bg-custom-primary borders p-2 row">
<div class="col-auto header-title mr-auto text-white">
							<h1 class="middle">Events List</h1>
						</div>
<div class="bg-custom-primary col-auto" onclick="closeEvents()">
<i class="fa-4x fa-times fas text-white"></i>
						</div>
					</div>
				</div>
<div class="borders card-body">
<div class="events-contents" id="htmlContent" style="overflow: scroll;">

				</div>
				</div>
			</div>
		</div>
<div id="evoCalendar" style="direction:ltr!important"></div>
	</div>

	<script>
		var emptyEvents = '<div class="event-empty"><div class="sheet taglib-empty-result-message" style=" margin-bottom: 0px; padding-top: 0px;"><div class="taglib-empty-result-message-header"></div></div></div>';
		var _schedules = [];
		$("#evoCalendar").evoCalendar({
			language: 'en',
			sidebarDisplayDefault: false,
			eventDisplayDefault: false,
			eventListToggler: false,
			calendarEvents: _calendarEvents
		});
		$("#evoCalendar").on('selectDate', function(item) {
			var events = $('#evoCalendar').evoCalendar('getActiveEvents');
			var htmlContent = "";
			events.forEach(function(item) {
				var name = item.name;
				var Description = item.description;
				var template = '<div class="card my-4" ><div class="card-header">'
				+ name +
				'</div><div class="card-body">' +
				Description +
					'</div></div>';
				htmlContent =htmlContent + template;
			});
			if(htmlContent == "" || htmlContent.length < 2)
			{
				htmlContent=emptyEvents;
			}
			$("#htmlContent").html(htmlContent);

			console.log(events);

			$(".events").addClass("scale-up-center");
			/* $("#eventListToggler").click();
			 $(".event-empty").html();
			 */
		});
		function closeEvents()
		{
			$(".events").removeClass("scale-up-center");
			$(".events").addClass("scale-down-center");
			setTimeout(function()
			{
				$(".events").removeClass("scale-down-center");
			}, 395);

		}
	</script>
	<#else>
		<div>
			<p>Please select a valid calendar events collection</p>
		</div>
</#if>
<style>
.scale-up-center {
	display:flex;
	-webkit-animation: scale-up-center 0.4s cubic-bezier(0.390, 0.575, 0.565, 1.000) both;
			animation: scale-up-center 0.4s cubic-bezier(0.390, 0.575, 0.565, 1.000) both;
}
@-webkit-keyframes scale-up-center{0%{-webkit-transform:scale(.5);transform:scale(.5)}100%{-webkit-transform:scale(1);transform:scale(1)}}@keyframes scale-up-center{0%{-webkit-transform:scale(.5);transform:scale(.5)}100%{-webkit-transform:scale(1);transform:scale(1)}}
.scale-down-center{ display:flex;-webkit-animation:scale-down-center .4s cubic-bezier(.25,.46,.45,.94) both;animation:scale-down-center .4s cubic-bezier(.25,.46,.45,.94) both}
@-webkit-keyframes scale-down-center{0%{-webkit-transform:scale(1);transform:scale(1)}100%{-webkit-transform:scale(.5);transform:scale(.5)}}@keyframes scale-down-center{0%{-webkit-transform:scale(1);transform:scale(1)}100%{-webkit-transform:scale(.5);transform:scale(.5)}}

</style>