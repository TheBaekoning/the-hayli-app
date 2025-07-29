<svelte:options runes={true} />
<script lang="ts">
	import Calendar from '$components/calendar/calendar.svelte';
</script>

<main>
	<header>
		<h1>Moods</h1>
	</header>
	<div class="mood-index__create">
		<a href="/health/mood/create">Add Mood Entry</a>
	</div>
	<div class="mood-index__moods">
		<Calendar />
	</div>
</main>