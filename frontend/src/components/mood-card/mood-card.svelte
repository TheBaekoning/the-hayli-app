<svelte:options runes={true} />
<script lang="ts">
	import MoodModel from '$lib/models/mood';
	import moodStore from '$lib/stores/moodStore';

	let { moodEntry }: { moodEntry: MoodModel} = $props();

	let optionsVisible: boolean = $state(false);
	let mood: string|string[] = $derived.by(() => {
		let mood: string|string[] = '';
		switch(moodEntry.rating){
			case 1:
				mood = ['😢', 'Very Bad'];
				break;
			case 2:
				mood = ['😕', 'Bad'];
				break;
			case 3:
				mood = ['😐', 'Neutral'];
				break;
			case 4:
				mood = ['😊', 'Good'];
				break;
			case 5:
				mood = ['😁', 'Very Good'];
		}
		return mood;
	});

	function toggleOptions(){
		optionsVisible = !optionsVisible;
	}
	function deleteMood(){
		moodStore.delete({ record: moodEntry }).then(() => {
			window.location.reload();
		}, console.error);
	}
</script>

<div class="mood-card">
	<div class="mood-card__header">
		<div class="mood-card__title"><a href={`/health/mood/edit/${moodEntry.id}`}>{moodEntry.date}</a></div>
		{#if moodEntry.rating}
			<div class="mood-card__rating">
				<span aria-hidden="true">{mood[0]}</span>
				<span class="visually-hidden">{mood[1]}</span>
			</div>
		{/if}
	</div>
	<!-- {#if moodEntry.notes}
		<div class="mood-card__notes">
			{moodEntry.notes}
		</div>
	{/if} -->
	<div class="mood-card__options" class:show-actions={optionsVisible}>
		<button class="mood-card__toggle not-button" onclick={toggleOptions}>
			<svg class="mood-card__icon options" aria-label="Options" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 80" width="20" height="80">
				<circle cx="10" cy="10" r="10" />
				<circle cx="10" cy="40" r="10" />
				<circle cx="10" cy="70" r="10" />
			</svg>
		</button>
		<div class="mood-card__actions">
			<button onclick={deleteMood}>
				Delete
			</button>
		</div>
	</div>
</div>

<style lang="scss">
	.mood-card{
		display: grid;
		grid-template-columns: auto min-content;
		padding: 20px;
		background: var(--light2);
		border-radius: 4px;
		a{
			color: var(--dark2);
		}
	}
	:global(.mood-card+.mood-card){
		margin-top: 20px;
	}
	.mood-card__header{
		display: flex;
		flex-flow: column;
		gap: 10px;
	}
	.mood-card__options{
		position: relative;
		&.show-actions{
			.mood-card__actions{
				max-height: 80px;
			}
		}
	}
	.mood-card__toggle{
		justify-self: end;
	}
	.mood-card__icon{
		height: auto;
		&.options{
			max-width: 5px;
		}
	}
	.mood-card__actions{
		position: absolute;
		top: 0;
		left: -80px;
		display: flex;
		gap: 5px;
		max-height: 0px;
		overflow: hidden;
		transition: max-height 0.3s ease;
	}
</style>