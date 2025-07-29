<script lang="ts">
	import { browser } from "$app/environment";
	import type MoodModel from "$lib/models/mood";
	import moods from "$lib/stores/moodStore";

	let { date = new Date() }: { date?: Date } = $props();
	
	let trigger = $state(1);
	const selectedMonth = $derived(!!trigger && date.getMonth());

	function numericPad(n: number): string {
		return n.toString().padStart(2, '0');
	}

	let monthLayout: Array<Array<{day: string, mood: MoodModel|null}>> = $state([]);
	function layoutDaysOfMonth(month: number, filteredMods: MoodModel[]): void {
		// layout the days of the month in a 6x7 grid in monthLayout and highlight days with moods and their ratings
		let daysInMonth = new Date(date.getFullYear(), month + 1, 0).getDate();
		let firstDay = new Date(date.getFullYear(), month, 1,).getDay();
		let lastDayIndex = 0;
		let day = 1;
		let dayIndex = 0;
		let week = [];
		for (let i = 0; i < 6; i++) {
			week = [];
			for (let j = 0; j < 7; j++) {
				if (i === 0 && j < firstDay) {
					week.push({ day: "", mood: null });
				} else if (dayIndex < daysInMonth) {
					let mood = filteredMods.find((m) => {
						let mDate = new Date(m.date);
						// adjust for local time
						mDate.setHours(mDate.getHours() + (mDate.getTimezoneOffset() / 60));
						return mDate.getDate() === day && mDate.getMonth() === month && mDate.getFullYear() === date.getFullYear();
					});
					week.push({ day: day.toString(), mood: mood });
					day++;
					dayIndex++;
					lastDayIndex = j;
				} else {
					week.push({ day: "", mood: null });
				}
			}
			// Only add the week if it has days with a non-empty day
			if (week.some((d) => d.day !== "")) {
				monthLayout.push(week);
			}
		}
		//monthLayout = monthLayout;
	}
	$effect(() => {
		if (!browser || !trigger) return;
		moods.findAll({ 
			start: new Date(date.getFullYear(), selectedMonth, 1), 
			end: new Date(date.getFullYear(), selectedMonth + 1, 0), 
			page: 1,
			pageSize: 31 
		}).then(filteredMoods => {
			filteredMoods.sort((a: MoodModel, b: MoodModel) => {
				// sort in ascending order
				return new Date(a.date).getTime() - new Date(b.date).getTime();
			});
			layoutDaysOfMonth(selectedMonth, filteredMoods);
		});
	});
</script>
<svelte:options runes={true} />

<div class="calendar-container">
	<div class="month-selector">
		<button onclick={() => {
			date.setMonth(date.getMonth() - 1);
			trigger++;
			monthLayout = [];
		}} class="not-button">&lt;</button>
		<span>{!!trigger && date.toLocaleString('default', { month: 'long', year: 'numeric' })}</span>
		<button onclick={() => {
			date.setMonth(date.getMonth() + 1);
			trigger++;
			monthLayout = [];
		}} class="not-button">&gt;</button>
	</div>
	<div class="calendar">
		{#if monthLayout.length}
			<div class="day">Su</div>
			<div class="day">M</div>
			<div class="day">Tu</div>
			<div class="day">W</div>
			<div class="day">Th</div>
			<div class="day">F</div>
			<div class="day">Sa</div>
		{/if}
		{#each monthLayout as week}
			{#each week as day}
				<a class={`day ${day.mood ? `mood${day.mood.rating}` : ''}`} href={day.mood ? `/health/mood/edit/${day.mood.id}` : `/health/mood/create?moodDate=${`${date.getFullYear()}-${numericPad(date.getMonth() + 1)}-${day.day}`}`}>
					<span>{day.day}</span>
					{#if day.mood}<span class="sr-visible">Mood rating: {day.mood.ratingText}</span>{/if}
				</a>
			{/each}
		{:else}
			<div class="">Loading…</div>
		{/each}
	</div>
</div>

<style lang="scss">
	.calendar-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 30px;
		margin-top: 20px;
		padding: 10px;
		border: 1.5px solid var(--green6);
		border-radius: 15px;
	}
	.month-selector {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 15px;
		button{
			color: inherit;
		}
	}

	.calendar {
		display: grid;
		grid-template-columns: repeat(7, 1fr);
		grid-auto-rows: auto;
		gap: 2px;
		width: 100%;
	}
	.day {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border-radius: 5px;
		--padding: 4px;
		padding: var(--padding);
		position: relative;
		text-decoration: none;
		color: #b2b2b2;
		border: 1px solid transparent;
		font: 500 0.8125rem var(--ff);
		span {
			font-size: 1.5rem;
		}
		&.mood1{
			background: var(--awful);
			&::after{
				background-image: url(/icons/awful.svg);
			}
		}
		&.mood2{
			background: var(--meh);
			&::after{
				background: url(/icons/meh.svg);
			}
		}
		&.mood3{
			background: var(--balanced);
			&::after{
				background: url(/icons/balanced.svg);
			}
		}
		&.mood4{
			background: var(--energized);
			&::after{
				background: url(/icons/energized.svg);
			}
		}
		&.mood5{
			background: var(--ecstatic);
			&::after{
				background: url(/icons/ecstatic.svg);
			}
		}
		&[class*="mood"] {
			color: white;
			&::after{
				content: '';
				display: block;
				--size: 8px;
				height: var(--size);
				width: var(--size);
				position: absolute;
				right: var(--padding);
				bottom: var(--padding);
				background-size: cover;
				background-position: center;
				background-repeat: no-repeat;
			}
		}
		&:not(:nth-child(n + 8)){
			color: var(--green6);

			&:hover, &:focus{
				border-color: transparent;
			}
		}
	}
	@media(prefers-color-scheme: dark){
		.calendar-container{
			border-color: var(--green1);
		}
		.day{
			&:not(:nth-child(n + 8)){
				color: var(--green1);
			}
		}
	}
	@media (min-width: 1000px){
		.day{
			font-size: 1.25rem;
			--padding: 10px;
			&[class*="mood"] {
				&::after{
					--size: 2rem;
				}
			}
			&:hover, &:focus{
				border-color: var(--green6);
			}
		}
		.calendar {
			grid-auto-rows: 4.5rem;
		}
		@media (prefers-color-scheme: dark){
			.day{
				&:hover, &:focus{
					border-color: var(--green1);
				}
			}
		}
	}
</style>